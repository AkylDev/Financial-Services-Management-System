package kz.projects.ams.services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.requests.TransferRequest;
import kz.projects.ams.exceptions.InsufficientFundsException;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.TransactionMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.Transaction;
import kz.projects.ams.models.User;
import kz.projects.ams.models.enums.TransactionType;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.repositories.TransactionRepository;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.UserService;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация {@link TransactionService} для управления транзакциями.
 * Обрабатывает депозиты, снятие средств, переводы и получение списка транзакций для текущего пользователя.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {

  private final AccountRepository accountRepository;
  private final TransactionRepository transactionRepository;
  private final TransactionMapper transactionMapper;
  private final UserService userService;
  private final NotificationEventProducer notificationEventProducer;

  private static final String TOPIC_NAME = "topic-transactions";

  /**
   * Выполняет операцию пополнения счета.
   * Проверяет наличие счета, соответствие пользователя и обновляет баланс.
   *
   * @param request запрос на пополнение счета
   * @return {@link TransactionDTO} объект, представляющий выполненную транзакцию
   * @throws UserAccountNotFoundException если указанный счет не найден
   * @throws IllegalArgumentException если текущий пользователь не имеет доступа к указанному счету
   */
  @Override
  public TransactionDTO deposit(TransactionRequest request) {
    validateAmount(request.amount());
    Account account = getValidatedAccount(request.accountId());
    validateUserAccess(account);

    account.setBalance(account.getBalance() + request.amount());
    accountRepository.save(account);

    Transaction transaction = saveTransaction(account, TransactionType.DEPOSIT, request.amount());
    publishEvent(account, request.amount(), "deposit");

    return transactionMapper.toDto(transaction);
  }

  /**
   * Выполняет операцию снятия средств со счета.
   * Проверяет наличие счета, соответствие пользователя и обновляет баланс.
   *
   * @param request запрос на снятие средств
   * @return {@link TransactionDTO} объект, представляющий выполненную транзакцию
   * @throws UserAccountNotFoundException если указанный счет не найден
   * @throws IllegalArgumentException если текущий пользователь не имеет доступа к указанному счету
   */
  @Override
  public TransactionDTO withdraw(TransactionRequest request) {
    validateAmount(request.amount());
    Account account = getValidatedAccount(request.accountId());
    validateUserAccess(account);

    if (account.getBalance() < request.amount()) {
      throw new InsufficientFundsException("Insufficient funds");
    }

    account.setBalance(account.getBalance() - request.amount());
    accountRepository.save(account);

    Transaction transaction = saveTransaction(account, TransactionType.WITHDRAWAL, request.amount());
    publishEvent(account, request.amount(), "withdrawal");

    return transactionMapper.toDto(transaction);
  }

  /**
   * Выполняет перевод средств между двумя счетами.
   * Проверяет наличие обоих счетов, соответствие пользователя и обновляет балансы.
   *
   * @param request запрос на перевод средств
   * @return {@link TransactionDTO} объект, представляющий выполненную транзакцию
   * @throws UserAccountNotFoundException если один из указанных счетов не найден
   * @throws IllegalArgumentException если текущий пользователь не имеет доступа к исходному счету
   */
  @Override
  public TransactionDTO transfer(TransferRequest request) {
    validateAmount(request.amount());

    Account fromAccount = getValidatedAccount(request.fromAccount());
    Account toAccount = getValidatedAccount(request.toAccount());

    validateUserAccess(fromAccount);

    if (fromAccount.getBalance() < request.amount()) {
      throw new InsufficientFundsException("Insufficient funds");
    }

    fromAccount.setBalance(fromAccount.getBalance() - request.amount());
    toAccount.setBalance(toAccount.getBalance() + request.amount());

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);

    Transaction transaction = saveTransaction(fromAccount, TransactionType.TRANSFER, request.amount());
    publishEvent(fromAccount, request.amount(), "transfer to account ID " + request.toAccount());

    return transactionMapper.toDto(transaction);
  }

  /**
   * Получает список транзакций для текущего пользователя.
   *
   * @return список {@link TransactionDTO} объектов, представляющих транзакции текущего пользователя
   */
  @Override
  @Transactional(readOnly = true)
  public List<TransactionDTO> getTransactions() {
    User currentUser = userService.getCurrentSessionUser();
    return transactionRepository.findAllByUserId(currentUser.getId())
            .stream()
            .map(transactionMapper::toDto)
            .collect(Collectors.toList());
  }

  private void validateAmount(double amount) {
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
  }

  private Account getValidatedAccount(Long accountId) {
    return accountRepository.findById(accountId)
            .orElseThrow(() -> new UserAccountNotFoundException("Account Not Found!"));
  }

  private void validateUserAccess(Account account) {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    if (!account.getUser().getId().equals(currentUserId)) {
      throw new UnauthorizedException("You are not allowed to access this account");
    }
  }

  private Transaction saveTransaction(Account account, TransactionType type, double amount) {
    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(type);
    transaction.setAmount(amount);
    transaction.setDate(new Date());
    return transactionRepository.save(transaction);
  }

  private void publishEvent(Account account, double amount, String operation) {
    NotificationEvent event = new NotificationEvent(
            userService.getCurrentSessionUser().getId().toString(),
            userService.getCurrentSessionUser().getName(),
            userService.getCurrentSessionUser().getEmail(),
            "Operation went successfully! " + "\n" +
                    "Operation: " + operation + "\n" +
                    "Amount: " + amount + "\n" +
                    "Account ID: " + account.getId() + "\n" +
                    "Account Type: " + account.getAccountType() + "\n",
            LocalDateTime.now().toString()
    );
    notificationEventProducer.publishEvent(event, TOPIC_NAME);
  }
}