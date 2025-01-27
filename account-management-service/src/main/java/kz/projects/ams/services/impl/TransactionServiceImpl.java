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
import kz.projects.ams.models.enums.TransactionType;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.repositories.TransactionRepository;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.UserService;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация {@link TransactionService} для управления транзакциями.
 * Обрабатывает депозиты, снятие средств, переводы и получение списка транзакций для текущего пользователя.
 */
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final AccountRepository accountRepository;

  private final TransactionRepository transactionRepository;

  private final TransactionMapper transactionMapper;

  private final UserService userService;

  private final NotificationEventProducer notificationEventProducer;

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
    if (request.amount() <= 0) {
      throw new IllegalArgumentException("Deposit amount must be greater than zero");
    }

    Optional<Account> accountOptional = accountRepository.findById(request.accountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account Not Found!");
    }

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(userService.getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not allowed");
    }

    account.setBalance(account.getBalance() + request.amount());
    accountRepository.save(account);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAmount(request.amount());
    transaction.setDate(new Date());

    Transaction savedTransaction = transactionRepository.save(transaction);

    publishEvent("You have successfully deposit " + request.amount() + " to your account with ID " + account.getId()
    + " and type " + account.getAccountType());
    return transactionMapper.toDto(savedTransaction);
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
    if (request.amount() <= 0) {
      throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
    }

    Optional<Account> accountOptional = accountRepository.findById(request.accountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account Not Found!");
    }

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(userService.getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not allowed");
    }

    if (account.getBalance() < request.amount()) {
      throw new InsufficientFundsException("Insufficient funds");
    }

    account.setBalance(account.getBalance() - request.amount());
    accountRepository.save(account);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setAmount(request.amount());
    transaction.setDate(new Date());

    Transaction savedTransaction = transactionRepository.save(transaction);

    publishEvent("You have successfully withdraw " + request.amount() + " from your account with ID " + account.getId()
            + " and type " + account.getAccountType());
    return transactionMapper.toDto(savedTransaction);
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
    if (request.amount() <= 0) {
      throw new IllegalArgumentException("Transfer amount must be greater than zero");
    }

    Optional<Account> fromAccountOptional = accountRepository.findById(request.fromAccount());
    Optional<Account> toAccountOptional = accountRepository.findById(request.toAccount());

    if (fromAccountOptional.isEmpty() || toAccountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account fromAccount = fromAccountOptional.get();
    Account toAccount = toAccountOptional.get();

    if (!fromAccount.getUser().getId().equals(userService.getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not allowed");
    }

    if (fromAccount.getBalance() < request.amount()) {
      throw new InsufficientFundsException("Insufficient funds");
    }

    fromAccount.setBalance(fromAccount.getBalance() - request.amount());
    toAccount.setBalance(toAccount.getBalance() + request.amount());

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);

    Transaction transaction = new Transaction();
    transaction.setAccount(fromAccount);
    transaction.setType(TransactionType.TRANSFER);
    transaction.setAmount(request.amount());
    transaction.setDate(new Date());

    Transaction savedTransaction = transactionRepository.save(transaction);

    publishEvent("You have successfully transfer " + request.amount() + " to your account with ID " + request.toAccount()
            + " from your account with ID " + request.fromAccount());
    return transactionMapper.toDto(savedTransaction);
  }

  /**
   * Получает список транзакций для текущего пользователя.
   *
   * @return список {@link TransactionDTO} объектов, представляющих транзакции текущего пользователя
   */
  @Override
  public List<TransactionDTO> getTransactions() {
    User currentUser = userService.getCurrentSessionUser();
    List<Transaction> transactions = transactionRepository.findAllByUserId(currentUser.getId());
    return transactions.stream()
            .map(transactionMapper::toDto)
            .collect(Collectors.toList());
  }

  private void publishEvent(String message) {
    NotificationEvent event = new NotificationEvent(
            userService.getCurrentSessionUser().getId().toString(),
            userService.getCurrentSessionUser().getUsername(),
            userService.getCurrentSessionUser().getEmail(),
            message,
            LocalDateTime.now().toString()
    );

    notificationEventProducer.publishEvent(event, "topic-account");
  }
}
