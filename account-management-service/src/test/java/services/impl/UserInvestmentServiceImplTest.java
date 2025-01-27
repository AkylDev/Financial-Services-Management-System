package services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.exceptions.InvestmentOperationException;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
import kz.projects.ams.models.enums.TransactionType;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.UserService;
import kz.projects.ams.services.impl.UserInvestmentServiceImpl;
import kz.projects.commonlib.dto.BalanceCheckRequest;
import kz.projects.commonlib.dto.BalanceCheckResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserInvestmentServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private TransactionService transactionService;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private NotificationEventProducer notificationEventProducer;

  @InjectMocks
  private UserInvestmentServiceImpl userInvestmentService;

  private MockWebServer mockWebServer;
  private ObjectMapper objectMapper;

  private User user;
  private Account account;
  private InvestmentRequest investmentRequest;
  private InvestmentResponse investmentResponse;
  private BalanceCheckRequest balanceCheckRequest;

  @BeforeEach
  public void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start();
    String baseUrl = mockWebServer.url("/").toString();
    userInvestmentService = new UserInvestmentServiceImpl(WebClient.builder().baseUrl(baseUrl), userService, transactionService, accountRepository, notificationEventProducer);

    objectMapper = new ObjectMapper();
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

    balanceCheckRequest = new BalanceCheckRequest(
            1L,
            500.0
    );
  }

  @AfterEach
  public void tearDown() throws Exception {
    mockWebServer.shutdown();
  }

  @Test
  public void testToInvest_Success() throws Exception {
    TransactionDTO transactionResponse = new TransactionDTO(
            1L,
            1L,
            TransactionType.DEPOSIT,
            0.0,
            new Date()
    );

    when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
    when(userService.getCurrentSessionUser()).thenReturn(user);
    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody(objectMapper.writeValueAsString(investmentResponse)));
    when(transactionService.withdraw(any(TransactionRequest.class))).thenReturn(transactionResponse);

    InvestmentResponse response = userInvestmentService.toInvest(investmentRequest);

    assertNotNull(response);
    assertEquals(investmentResponse.id(), response.id());
    assertEquals(investmentResponse.amount(), response.amount(), 0.0);
  }

  @Test
  public void testToInvest_WebClientResponseException() {
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(userService.getCurrentSessionUser()).thenReturn(user);
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(InvestmentOperationException.class, () -> userInvestmentService.toInvest(investmentRequest));
  }

  @Test
  public void testDeleteInvestment_Success() {
    when(userService.getCurrentSessionUser()).thenReturn(user);

    mockWebServer.enqueue(new MockResponse().setResponseCode(204));

    assertDoesNotThrow(() -> userInvestmentService.deleteInvestment(1L));
  }

  @Test
  public void testDeleteInvestment_WebClientResponseException() {
    when(userService.getCurrentSessionUser()).thenReturn(user);
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(InvestmentOperationException.class, () -> userInvestmentService.deleteInvestment(1L));
  }

  @Test
  public void testGetAllUsersInvestments_Success() throws Exception {
    when(userService.getCurrentSessionUser()).thenReturn(user);
    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader("Content-Type", "application/json")
            .setBody(objectMapper.writeValueAsString(Collections.singletonList(investmentResponse))));

    List<InvestmentResponse> investments = userInvestmentService.getAllUsersInvestments();

    assertNotNull(investments);
    assertEquals(1, investments.size());
  }

  @Test
  public void testGetAllUsersInvestments_WebClientResponseException() {
    when(userService.getCurrentSessionUser()).thenReturn(user);
    mockWebServer.enqueue(new MockResponse().setResponseCode(500));

    assertThrows(InvestmentOperationException.class, () -> userInvestmentService.getAllUsersInvestments());
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
    when(userService.getCurrentSessionUser()).thenReturn(user);

    assertThrows(UnauthorizedException.class, () -> userInvestmentService.toInvest(investmentRequest));
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

