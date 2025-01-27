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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Реализация {@link AccountService} для управления аккаунтами.
 * Обрабатывает создание, поиск, обновление и удаление аккаунтов для текущего пользователя.
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  private final AccountMapper accountMapper;

  private final UserService userService;

  private final NotificationEventProducer notificationEventProducer;

  /**
   * Создает новый аккаунт для текущего пользователя.
   *
   * @param accountRequest {@link AccountDTO} объект, содержащий данные для создания аккаунта
   * @return {@link AccountDTO} объект, представляющий созданный аккаунт
   */
  @Override
  public AccountDTO createAccount(AccountDTO accountRequest) {
    Account account = new Account();
    account.setUser(userService.getCurrentSessionUser());
    account.setAccountType(accountRequest.accountType());
    account.setBalance(accountRequest.balance());

    publishEvent("You have successfully created account with type " + accountRequest.accountType() +
            " and balance " + accountRequest.balance());

    return accountMapper.toDto(accountRepository.save(account));
  }

  /**
   * Находит все аккаунты текущего пользователя.
   *
   * @return список {@link AccountDTO} объектов, представляющих аккаунты текущего пользователя
   */
  @Override
  public List<AccountDTO> findAccountsByUserId() {
    User currentUser = userService.getCurrentSessionUser();
    List<Account> accounts = accountRepository.findAllByUser(currentUser);
    return accounts.stream()
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
    if (request.balance() < 0) {
      throw new IllegalArgumentException("Balance cannot be negative");
    }

    Optional<Account> accountOptional = accountRepository.findById(id);
    if (accountOptional.isEmpty()) {
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(userService.getCurrentSessionUser().getId())) {
      throw new UnauthorizedException("You are not authorized to change this account");
    }

    account.setAccountType(request.accountType());
    account.setBalance(request.balance());

    publishEvent("You have successfully updated your account with id " + account.getId() +
            " to type " + account.getAccountType() +
            " and balance " + account.getBalance());

    return accountMapper.toDto(accountRepository.save(account));
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
    Optional<Account> accountOptional = accountRepository.findById(id);
    if (accountOptional.isEmpty()) {
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(userService.getCurrentSessionUser().getId())) {
      throw new UnauthorizedException("You are not authorized to delete this account");
    }

    accountRepository.deleteById(id);

    publishEvent("You have successfully deleted your account with id " + account.getId() +
            " type " + account.getAccountType() +
            " and balance " + account.getBalance());
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
