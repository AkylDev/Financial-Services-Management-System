package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.AccountMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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

  /**
   * Получает текущего авторизованного пользователя из контекста безопасности.
   *
   * @return {@link User} текущий авторизованный пользователь, или {@code null}, если нет активной авторизации
   */
  public User getCurrentSessionUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      return (User) authentication.getPrincipal();
    }
    return null;
  }

  /**
   * Создает новый аккаунт для текущего пользователя.
   *
   * @param accountRequest {@link AccountDTO} объект, содержащий данные для создания аккаунта
   * @return {@link AccountDTO} объект, представляющий созданный аккаунт
   */
  @Override
  public AccountDTO createAccount(AccountDTO accountRequest) {
    Account account = new Account();
    account.setUser(getCurrentSessionUser());
    account.setAccountType(accountRequest.accountType());
    account.setBalance(accountRequest.balance());

    return accountMapper.toDto(accountRepository.save(account));
  }

  /**
   * Находит все аккаунты текущего пользователя.
   *
   * @return список {@link AccountDTO} объектов, представляющих аккаунты текущего пользователя
   */
  @Override
  public List<AccountDTO> findAccountsByUserId() {
    User currentUser = getCurrentSessionUser();
    List<Account> accounts = accountRepository.findAllByUser(currentUser);
    return accounts.stream()
            .map(accountMapper::toDto)
            .collect(Collectors.toList());
  }

  /**
   * Обновляет данные аккаунта с указанным идентификатором.
   * Проверяет права текущего пользователя на изменение аккаунта.
   *
   * @param id идентификатор аккаунта
   * @param request {@link AccountDTO} объект, содержащий обновленные данные аккаунта
   * @return {@link AccountDTO} объект, представляющий обновленный аккаунт
   * @throws UserAccountNotFoundException если аккаунт с указанным идентификатором не найден
   * @throws UnauthorizedException если текущий пользователь не авторизован для изменения аккаунта
   */
  @Override
  public AccountDTO updateAccount(Long id, AccountDTO request) {
    if (request.balance() < 0) {
      throw new IllegalArgumentException("Balance cannot be negative");
    }

    Optional<Account> accountOptional = accountRepository.findById(id);
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not authorized to change this account");
    }

    account.setAccountType(request.accountType());
    account.setBalance(request.balance());

    return accountMapper.toDto(accountRepository.save(account));
  }

  /**
   * Удаляет аккаунт с указанным идентификатором.
   * Проверяет права текущего пользователя на удаление аккаунта.
   *
   * @param id идентификатор аккаунта
   * @throws UserAccountNotFoundException если аккаунт с указанным идентификатором не найден
   * @throws UnauthorizedException если текущий пользователь не авторизован для удаления аккаунта
   */
  @Override
  public void deleteAccount(Long id) {
    Optional<Account> accountOptional = accountRepository.findById(id);
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not authorized to delete this account");
    }

    accountRepository.deleteById(id);
  }
}
