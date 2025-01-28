package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.AccountMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.UserService;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация {@link AccountService} для управления аккаунтами.
 * Обрабатывает создание, поиск, обновление и удаление аккаунтов для текущего пользователя.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;
  private final AccountMapper accountMapper;
  private final UserService userService;
  private final NotificationEventProducer notificationEventProducer;

  private static final String TOPIC_NAME = "topic-account";

  /**
   * Создает новый аккаунт для текущего пользователя.
   *
   * @param accountRequest {@link AccountDTO} объект, содержащий данные для создания аккаунта
   * @return {@link AccountDTO} объект, представляющий созданный аккаунт
   */
  @Override
  public AccountDTO createAccount(AccountDTO accountRequest) {
    validateBalance(accountRequest.balance());

    Account account = new Account();
    account.setUser(userService.getCurrentSessionUser());
    account.setAccountType(accountRequest.accountType());
    account.setBalance(accountRequest.balance());

    Account savedAccount = accountRepository.save(account);
    publishEvent(savedAccount, "created");

    return accountMapper.toDto(savedAccount);
  }

  /**
   * Находит все аккаунты текущего пользователя.
   *
   * @return список {@link AccountDTO} объектов, представляющих аккаунты текущего пользователя
   */
  @Override
  @Transactional(readOnly = true)
  public List<AccountDTO> findAccountsByUserId() {
    User currentUser = userService.getCurrentSessionUser();
    return accountRepository.findAllByUser(currentUser).stream()
            .map(accountMapper::toDto)
            .collect(Collectors.toList());
  }

  /**
   * Обновляет данные аккаунта с указанным идентификатором.
   * Проверяет права текущего пользователя на изменение аккаунта.
   *
   * @param id      идентификатор аккаунта
   * @param request {@link AccountDTO} объект, содержащий обновленные данные аккаунта
   * @return {@link AccountDTO} объект, представляющий обновленный аккаунт
   * @throws UserAccountNotFoundException если аккаунт с указанным идентификатором не найден
   * @throws UnauthorizedException        если текущий пользователь не авторизован для изменения аккаунта
   */
  @Override
  public AccountDTO updateAccount(Long id, AccountDTO request) {
    validateBalance(request.balance());

    Account account = getValidatedAccount(id);
    validateUserAccess(account);

    account.setAccountType(request.accountType());
    account.setBalance(request.balance());

    Account updatedAccount = accountRepository.save(account);
    publishEvent(updatedAccount, "updated");

    return accountMapper.toDto(updatedAccount);
  }

  /**
   * Удаляет аккаунт с указанным идентификатором.
   * Проверяет права текущего пользователя на удаление аккаунта.
   *
   * @param id идентификатор аккаунта
   * @throws UserAccountNotFoundException если аккаунт с указанным идентификатором не найден
   * @throws UnauthorizedException        если текущий пользователь не авторизован для удаления аккаунта
   */
  @Override
  public void deleteAccount(Long id) {
    Account account = getValidatedAccount(id);
    validateUserAccess(account);

    accountRepository.delete(account);
    publishEvent(account, "deleted");
  }

  private void validateBalance(double balance) {
    if (balance < 0) {
      throw new IllegalArgumentException("Balance cannot be negative");
    }
  }

  private Account getValidatedAccount(Long id) {
    return accountRepository.findById(id)
            .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));
  }

  private void validateUserAccess(Account account) {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    if (!account.getUser().getId().equals(currentUserId)) {
      throw new UnauthorizedException("You are not authorized to access this account");
    }
  }

  private void publishEvent(Account account, String action) {
    String message = String.format(
            "You have successfully %s your account with id %d, type %s, and balance %.2f",
            action, account.getId(), account.getAccountType(), account.getBalance()
    );

    NotificationEvent event = new NotificationEvent(
            userService.getCurrentSessionUser().getId().toString(),
            userService.getCurrentSessionUser().getName(),
            userService.getCurrentSessionUser().getEmail(),
            message,
            LocalDateTime.now().toString()
    );

    notificationEventProducer.publishEvent(event, TOPIC_NAME);
  }
}