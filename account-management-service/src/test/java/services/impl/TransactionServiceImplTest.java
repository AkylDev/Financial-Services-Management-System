package services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.requests.TransferRequest;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.TransactionMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.Transaction;
import kz.projects.ams.models.User;
import kz.projects.ams.models.enums.TransactionType;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.repositories.TransactionRepository;
import kz.projects.ams.services.UserService;
import kz.projects.ams.services.impl.TransactionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private TransactionRepository transactionRepository;

  @Mock
  private TransactionMapper transactionMapper;

  @Mock
  private UserService userService;

  @InjectMocks
  private TransactionServiceImpl transactionService;

  @Test
  public void testDeposit() {
    TransactionRequest request = new TransactionRequest(1L, 100.0);

    User currentUser = new User();
    currentUser.setId(1L);

    Account account = new Account();
    account.setId(1L);
    account.setUser(currentUser);
    account.setBalance(500.0);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAmount(100.0);
    transaction.setDate(new Date());

    TransactionDTO transactionDTO = new TransactionDTO(
            1L,
            1L,
            TransactionType.DEPOSIT,
            100.0,
            new Date()
    );

    Mockito.when(accountRepository.findById(request.accountId())).thenReturn(Optional.of(account));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);
    Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);
    Mockito.when(transactionMapper.toDto(transaction)).thenReturn(transactionDTO);

    TransactionDTO result = transactionService.deposit(request);

    assertNotNull(result);
    assertEquals(transactionDTO.accountId(), result.accountId());
    assertEquals(transactionDTO.amount(), result.amount());
    assertEquals(transactionDTO.type(), result.type());
  }


  @Test(expected = UserAccountNotFoundException.class)
  public void testDepositAccountNotFound() {
    TransactionRequest request = new TransactionRequest(
            999L,
            100.0
    );

    Mockito.when(accountRepository.findById(request.accountId())).thenReturn(Optional.empty());

    transactionService.deposit(request);
  }

  @Test(expected = UnauthorizedException.class)
  public void testDepositUnauthorized() {
    TransactionRequest request = new TransactionRequest(
            999L,
            100.0
    );

    User currentUser = new User();
    currentUser.setId(2L);

    User accountUser = new User();
    accountUser.setId(1L);

    Account account = new Account();
    account.setId(1L);
    account.setUser(accountUser);
    account.setBalance(500.0);

    Mockito.when(accountRepository.findById(request.accountId())).thenReturn(Optional.of(account));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    transactionService.deposit(request);
  }

  @Test
  public void testWithdraw() {
    TransactionRequest request = new TransactionRequest(1L, 100.0);

    User currentUser = new User();
    currentUser.setId(1L);

    Account account = new Account();
    account.setId(1L);
    account.setUser(currentUser);
    account.setBalance(500.0);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setAmount(100.0);
    transaction.setDate(new Date());

    TransactionDTO transactionDTO = new TransactionDTO(
            1L,
            1L,
            TransactionType.WITHDRAWAL,
            100.0,
            new Date()
    );

    Mockito.when(accountRepository.findById(request.accountId())).thenReturn(Optional.of(account));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);
    Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);
    Mockito.when(transactionMapper.toDto(transaction)).thenReturn(transactionDTO);

    TransactionDTO result = transactionService.withdraw(request);

    assertNotNull(result);
    assertEquals(transactionDTO.accountId(), result.accountId());
    assertEquals(transactionDTO.amount(), result.amount());
    assertEquals(transactionDTO.type(), result.type());
  }


  @Test(expected = UserAccountNotFoundException.class)
  public void testWithdrawAccountNotFound() {
    TransactionRequest request = new TransactionRequest(
            999L,
            100.0
    );

    Mockito.when(accountRepository.findById(request.accountId())).thenReturn(Optional.empty());

    transactionService.withdraw(request);
  }

  @Test(expected = UnauthorizedException.class)
  public void testWithdrawUnauthorized() {
    TransactionRequest request = new TransactionRequest(
            999L,
            100.0
    );

    User currentUser = new User();
    currentUser.setId(2L);

    User accountUser = new User();
    accountUser.setId(1L);

    Account account = new Account();
    account.setId(1L);
    account.setUser(accountUser);
    account.setBalance(500.0);

    Mockito.when(accountRepository.findById(request.accountId())).thenReturn(Optional.of(account));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    transactionService.withdraw(request);
  }

  @Test
  public void testTransfer() {
    TransferRequest request = new TransferRequest(1L, 2L, 100.0);

    User currentUser = new User();
    currentUser.setId(1L);

    Account fromAccount = new Account();
    fromAccount.setId(1L);
    fromAccount.setUser(currentUser);
    fromAccount.setBalance(500.0);

    Account toAccount = new Account();
    toAccount.setId(2L);
    toAccount.setBalance(300.0);

    Transaction transaction = new Transaction();
    transaction.setAccount(fromAccount);
    transaction.setType(TransactionType.TRANSFER);
    transaction.setAmount(100.0);
    transaction.setDate(new Date());

    TransactionDTO transactionDTO = new TransactionDTO(
            1L,
            1L,
            TransactionType.TRANSFER,
            100.0,
            new Date()
    );

    Mockito.when(accountRepository.findById(request.fromAccount())).thenReturn(Optional.of(fromAccount));
    Mockito.when(accountRepository.findById(request.toAccount())).thenReturn(Optional.of(toAccount));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);
    Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transaction);
    Mockito.when(transactionMapper.toDto(transaction)).thenReturn(transactionDTO);

    TransactionDTO result = transactionService.transfer(request);

    assertNotNull(result);
    assertEquals(transactionDTO.accountId(), result.accountId());
    assertEquals(transactionDTO.amount(), result.amount());
    assertEquals(transactionDTO.type(), result.type());
  }


  @Test(expected = UserAccountNotFoundException.class)
  public void testTransferAccountNotFound() {
    TransferRequest request = new TransferRequest(
            1L,
            2L,
            100.
    );

    Mockito.when(accountRepository.findById(request.fromAccount())).thenReturn(Optional.empty());
    Mockito.when(accountRepository.findById(request.toAccount())).thenReturn(Optional.empty());

    transactionService.transfer(request);
  }

  @Test(expected = UnauthorizedException.class)
  public void testTransferUnauthorized() {
    TransferRequest request = new TransferRequest(
            1L,
            2L,
            100.
    );

    User currentUser = new User();
    currentUser.setId(2L);

    User accountUser = new User();
    accountUser.setId(1L);

    Account fromAccount = new Account();
    fromAccount.setId(1L);
    fromAccount.setUser(accountUser);
    fromAccount.setBalance(500.0);

    Account toAccount = new Account();
    toAccount.setId(2L);
    toAccount.setBalance(300.0);

    Mockito.when(accountRepository.findById(request.fromAccount())).thenReturn(Optional.of(fromAccount));
    Mockito.when(accountRepository.findById(request.toAccount())).thenReturn(Optional.of(toAccount));
    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);

    transactionService.transfer(request);
  }

  @Test
  public void testGetTransactions() {
    User currentUser = new User();
    currentUser.setId(1L);

    Transaction transaction = new Transaction();
    transaction.setAccount(new Account());
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAmount(100.0);
    transaction.setDate(new Date());

    List<Transaction> transactions = Collections.singletonList(transaction);
    TransactionDTO transactionDTO = new TransactionDTO(
            1L,
            1L,
            TransactionType.DEPOSIT,
            100.0,
            new Date()
    );

    Mockito.when(userService.getCurrentSessionUser()).thenReturn(currentUser);
    Mockito.when(transactionRepository.findAllByUserId(currentUser.getId())).thenReturn(transactions);
    Mockito.when(transactionMapper.toDto(transaction)).thenReturn(transactionDTO);

    List<TransactionDTO> result = transactionService.getTransactions();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(transactionDTO.accountId(), result.get(0).accountId());
    assertEquals(transactionDTO.amount(), result.get(0).amount());
    assertEquals(transactionDTO.type(), result.get(0).type());
  }

}
