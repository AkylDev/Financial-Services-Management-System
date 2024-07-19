package services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.exceptions.InvestmentOperationException;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.impl.UserInvestmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class UserInvestmentServiceImplTest {

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private AccountService accountService;

  @Mock
  private TransactionService transactionService;

  @Mock
  private AccountRepository accountRepository;

  @InjectMocks
  private UserInvestmentServiceImpl userInvestmentService;

  private User user;
  private Account account;
  private InvestmentRequest investmentRequest;
  private InvestmentResponse investmentResponse;
  private TransactionRequest transactionRequest;
  private BalanceCheckRequest balanceCheckRequest;

  @BeforeEach
  public void setUp() {
    user = new User();
    user.setId(1L);

    account = new Account();
    account.setId(1L);
    account.setUser(user);
    account.setBalance(1000.0);

    investmentRequest = new InvestmentRequest(
            1L, user.getId(), account.getId(), null, 500.0
    );

    investmentResponse = new InvestmentResponse(
            1L,
            1L,
            null,
            500.0,
            new Date()
    );

    transactionRequest = new TransactionRequest(
            1L,
            500.0
    );

    balanceCheckRequest = new BalanceCheckRequest(
            1L,
            500.0
    );
  }

  @Test
  public void testToInvest_Success() {
    when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
    when(accountService.getCurrentSessionUser()).thenReturn(user);
    when(restTemplate.postForObject(eq("http://localhost:8092/investments"),
            any(InvestmentRequest.class), eq(InvestmentResponse.class)))
            .thenReturn(investmentResponse);

    InvestmentResponse response = userInvestmentService.toInvest(investmentRequest);

    ArgumentCaptor<TransactionRequest> captor = ArgumentCaptor.forClass(TransactionRequest.class);
    verify(transactionService).withdraw(captor.capture());
    TransactionRequest capturedRequest = captor.getValue();

    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals(500.0, response.amount());

    assertEquals(transactionRequest.accountId(), capturedRequest.accountId());
    assertEquals(transactionRequest.amount(), capturedRequest.amount());
  }

  @Test
  public void testToInvest_AccountNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(UserAccountNotFoundException.class, () -> userInvestmentService.toInvest(investmentRequest));
  }

  @Test
  public void testToInvest_Unauthorized() {
    User anotherUser = new User();
    anotherUser.setId(2L);
    account.setUser(anotherUser);

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountService.getCurrentSessionUser()).thenReturn(user);

    assertThrows(UnauthorizedException.class, () -> userInvestmentService.toInvest(investmentRequest));
  }

  @Test
  public void testToInvest_RestClientException() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountService.getCurrentSessionUser()).thenReturn(user);
    when(restTemplate.postForObject(eq("http://localhost:8092/investments"), any(InvestmentRequest.class), eq(InvestmentResponse.class)))
            .thenThrow(RestClientException.class);

    assertThrows(InvestmentOperationException.class, () -> userInvestmentService.toInvest(investmentRequest));
  }

  @Test
  public void testUpdateInvestment_Success() {
    when(accountService.getCurrentSessionUser()).thenReturn(user);

    assertDoesNotThrow(() -> userInvestmentService.updateInvestment(1L, investmentRequest));
  }

  @Test
  public void testUpdateInvestment_RestClientException() {
    when(accountService.getCurrentSessionUser()).thenReturn(user);
    doThrow(RestClientException.class).when(restTemplate).put(eq("http://localhost:8092/investments"), any(InvestmentRequest.class), eq(AdvisorySessionDTO.class));

    assertThrows(AdvisorySessionOrderException.class, () -> userInvestmentService.updateInvestment(1L, investmentRequest));
  }

  @Test
  public void testDeleteInvestment_Success() {
    when(accountService.getCurrentSessionUser()).thenReturn(user);

    assertDoesNotThrow(() -> userInvestmentService.deleteInvestment(1L));
  }

  @Test
  public void testDeleteInvestment_RestClientException() {
    when(accountService.getCurrentSessionUser()).thenReturn(user);
    doThrow(RestClientException.class).when(restTemplate).delete(eq("http://localhost:8092/investments/{id}?userId={userId}"), eq(1L), eq(1L));

    assertThrows(AdvisorySessionOrderException.class, () -> userInvestmentService.deleteInvestment(1L));
  }

  @Test
  public void testGetAllUsersInvestments_Success() {
    when(accountService.getCurrentSessionUser()).thenReturn(user);
    ResponseEntity<List<InvestmentResponse>> responseEntity = ResponseEntity.ok(Collections.singletonList(investmentResponse));
    when(restTemplate.exchange(eq("http://localhost:8092/investments?userId=1"), eq(HttpMethod.GET), eq(null),
            any(ParameterizedTypeReference.class)))
            .thenReturn(responseEntity);

    List<InvestmentResponse> investments = userInvestmentService.getAllUsersInvestments();

    assertNotNull(investments);
    assertEquals(1, investments.size());
  }

  @Test
  public void testGetAllUsersInvestments_RestClientException() {
    when(accountService.getCurrentSessionUser()).thenReturn(user);
    when(restTemplate.exchange(eq("http://localhost:8092/investments?userId=1"), eq(HttpMethod.GET),
            eq(null), any(ParameterizedTypeReference.class)))
            .thenThrow(RestClientException.class);

    assertThrows(AdvisorySessionOrderException.class, () -> userInvestmentService.getAllUsersInvestments());
  }

  @Test
  public void testCheckBalance_Success() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

    BalanceCheckResponse response = userInvestmentService.checkBalance(balanceCheckRequest);

    assertNotNull(response);
    assertTrue(response.sufficientFunds());
    assertEquals(1000.0, response.currentBalance());
  }

  @Test
  public void testCheckBalance_AccountNotFound() {
    when(accountRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(UserAccountNotFoundException.class, () -> userInvestmentService.checkBalance(balanceCheckRequest));
  }
}

