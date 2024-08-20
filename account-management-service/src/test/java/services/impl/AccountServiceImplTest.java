package services.impl;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.AccountMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
import kz.projects.ams.models.enums.AccountType;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.UserService;
import kz.projects.ams.services.impl.AccountServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class AccountServiceImplTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private UserService userService;

  @InjectMocks
  private AccountServiceImpl accountService;

  @Before
  public void setup() {
    User currentUser = new User();
    currentUser.setId(1L);
    Mockito.lenient().when(authentication.getPrincipal()).thenReturn(currentUser);
    Mockito.lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    SecurityContextHolder.setContext(securityContext);
  }

  @Test
  public void testCreateAccount() {
    AccountDTO accountDTO = new AccountDTO(
            null,
            "test@example.com",
            AccountType.SAVINGS,
            100.0
    );

    Account account = new Account();
    account.setId(1L);
    Mockito.when(accountMapper.toDto(Mockito.any(Account.class))).thenReturn(accountDTO);
    Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenReturn(account);

    AccountDTO createdAccount = accountService.createAccount(accountDTO);

    assertNotNull(createdAccount);
    assertEquals(accountDTO.email(), createdAccount.email());
    assertEquals(accountDTO.accountType(), createdAccount.accountType());
    assertEquals(accountDTO.balance(), createdAccount.balance());
  }

  @Test
  public void testFindAccountsByUserId() {
    User currentUser = new User();
    currentUser.setId(1L);
    Mockito.lenient().when(accountRepository.findAllByUser(currentUser)).thenReturn(Collections.emptyList());

    List<AccountDTO> foundAccounts = accountService.findAccountsByUserId();

    assertNotNull(foundAccounts);
    assertTrue(foundAccounts.isEmpty());
  }

  @Test
  public void testUpdateAccount() {
    AccountDTO request = new AccountDTO(
            null,
            null,
            AccountType.INCOME,
            500.0
    );

    User currentUser = new User();
    currentUser.setId(1L);
    currentUser.setEmail("testEmail");

    Long accountId = 1L;
    Account existingAccount = new Account();
    existingAccount.setId(accountId);
    existingAccount.setUser(currentUser);
    existingAccount.setAccountType(AccountType.SAVINGS);
    existingAccount.setBalance(300.0);

    Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
    Mockito.when(accountRepository.save(Mockito.any(Account.class))).thenReturn(existingAccount);
    Mockito.when(accountMapper.toDto(Mockito.any(Account.class))).thenReturn(request);
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    AccountDTO updatedAccount = accountService.updateAccount(accountId, request);

    assertNotNull(updatedAccount);
    assertEquals(request.id(), updatedAccount.id());
    assertEquals(request.accountType(), updatedAccount.accountType());
    assertEquals(request.balance(), updatedAccount.balance());
  }

  @Test(expected = UserAccountNotFoundException.class)
  public void testUpdateAccountNotFound() {
    Long nonExistentAccountId = 999L;
    AccountDTO accountDTO = new AccountDTO(
            nonExistentAccountId,
            "email",
            AccountType.INCOME,
            500.0
    );

    Mockito.when(accountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

    accountService.updateAccount(nonExistentAccountId, accountDTO);
  }

  @Test(expected = UnauthorizedException.class)
  public void testUpdateAccountUnauthorized() {
    Long accountId = 1L;
    AccountDTO accountDTO = new AccountDTO(
            null,
            "email",
            AccountType.INCOME,
            500.0
    );

    User currentUser = new User();
    currentUser.setId(1L);
    currentUser.setEmail("testEmail");

    User accountUserId = new User();
    accountUserId.setId(2L);

    Account existingAccount = new Account();
    existingAccount.setId(accountId);
    existingAccount.setUser(accountUserId); // Mock different user ID than authenticated user
    existingAccount.setAccountType(AccountType.EXPENSES);
    existingAccount.setBalance(300.0);

    Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    accountService.updateAccount(accountId, accountDTO);
  }

  @Test
  public void testDeleteAccount() {
    User currentUser = new User();
    currentUser.setId(1L);

    Long accountId = 1L;
    Account existingAccount = new Account();
    existingAccount.setId(accountId);
    existingAccount.setUser(currentUser);
    existingAccount.setAccountType(AccountType.SAVINGS);
    existingAccount.setBalance(100.0);

    Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    accountService.deleteAccount(accountId);

    Mockito.verify(accountRepository, Mockito.times(1)).deleteById(accountId);
  }

  @Test(expected = UserAccountNotFoundException.class)
  public void testDeleteAccountNotFound() {
    Long nonExistentAccountId = 999L;

    Mockito.when(accountRepository.findById(nonExistentAccountId)).thenReturn(Optional.empty());

    accountService.deleteAccount(nonExistentAccountId);
  }

  @Test(expected = UnauthorizedException.class)
  public void testDeleteAccountUnauthorized() {
    User currentUser = new User();
    currentUser.setId(1L);
    currentUser.setEmail("testEmail");

    User accountUserId = new User();
    accountUserId.setId(2L);

    Long accountId = 1L;
    Account existingAccount = new Account();
    existingAccount.setId(accountId);
    existingAccount.setUser(accountUserId);
    existingAccount.setAccountType(AccountType.SAVINGS);
    existingAccount.setBalance(100.0);

    Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(existingAccount));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    accountService.deleteAccount(accountId);
  }
}


