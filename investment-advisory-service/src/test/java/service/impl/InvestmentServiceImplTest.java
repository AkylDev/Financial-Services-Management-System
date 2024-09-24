package service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.projects.ias.dto.BalanceCheckResponse;
import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.exceptions.NotSufficientFundsException;
import kz.projects.ias.mapper.InvestmentsMapper;
import kz.projects.ias.models.CustomerServiceRequest;
import kz.projects.ias.models.Investment;
import kz.projects.ias.models.enums.InvestmentType;
import kz.projects.ias.models.enums.RequestType;
import kz.projects.ias.repositories.InvestmentRepository;
import kz.projects.ias.service.CustomerRequestService;
import kz.projects.ias.service.impl.InvestmentServiceImpl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class InvestmentServiceImplTest {

  @Mock
  private InvestmentRepository investmentRepository;

  @Mock
  private CustomerRequestService customerRequestService;

  @InjectMocks
  private InvestmentServiceImpl investmentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateInvestment_Success() throws IOException, InterruptedException {
    MockWebServer mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(mockWebServer.url("/").toString());
    investmentService = new InvestmentServiceImpl(investmentRepository, webClientBuilder, customerRequestService);

    BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse(
            true,
            1000.0
    );
    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(new ObjectMapper().writeValueAsString(balanceCheckResponse))
    );

    InvestmentDTO request = new InvestmentDTO(
            1L,
            1L,
            1L,
            InvestmentType.STOCKS,
            500.0,
            new Date()
    );

    Investment investment = InvestmentsMapper.toEntity(request);
    investment.setId(1L);

    when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

    CustomerServiceRequest serviceRequest = new CustomerServiceRequest();
    serviceRequest.setUserId(request.userId());
    serviceRequest.setRequestType(RequestType.INVESTMENT);
    serviceRequest.setDescription("Customer invested " + request.amount()
            + "$ to " + request.investmentType());
    when(customerRequestService.createRequest(any(CustomerServiceRequest.class))).thenReturn(serviceRequest);

    InvestmentDTO result = investmentService.createInvestment(request);

    assertNotNull(result);
    assertEquals(investment.getId(), result.id());
    assertEquals(investment.getUserId(), result.userId());
    assertEquals(investment.getInvestmentType(), result.investmentType());
    assertEquals(investment.getAmount(), result.amount());

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/check-balance", recordedRequest.getPath());

    mockWebServer.shutdown();
  }

  @Test
  void testCreateInvestment_InsufficientFunds() throws IOException, InterruptedException {
    MockWebServer mockWebServer = new MockWebServer();
    mockWebServer.start();

    WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(mockWebServer.url("/").toString());
    investmentService = new InvestmentServiceImpl(investmentRepository, webClientBuilder, customerRequestService);

    BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse(
            false,
            null
    );


    mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .setBody(new ObjectMapper().writeValueAsString(balanceCheckResponse))
    );


    InvestmentDTO request = new InvestmentDTO(
            1L,
            1L,
            1L,
            InvestmentType.STOCKS,
            1500.0,
            new Date()
    );

    assertThrows(NotSufficientFundsException.class, () -> investmentService.createInvestment(request));

    RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
    assertNotNull(recordedRequest);
    assertEquals("/check-balance", recordedRequest.getPath());

    mockWebServer.shutdown();
  }

  @Test
  void testGetAllInvestments() {
    Long userId = 1L;
    List<Investment> investments = Arrays.asList(
            new Investment(1L, userId, InvestmentType.STOCKS, 1000.0, new Date()),
            new Investment(2L, userId, InvestmentType.BOND, 500.0, new Date())
    );

    when(investmentRepository.findAllByUserId(userId)).thenReturn(investments);

    List<Investment> result = investmentService.getAllInvestments(userId);

    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  void testUpdateInvestment() {
    InvestmentDTO request = new InvestmentDTO(
            1L,
            1L,
            1L,
            InvestmentType.STOCKS,
            800.0,
            new Date()
    );

    Investment existingInvestment = new Investment(1L, 1L, InvestmentType.STOCKS, 500.0, new Date());
    Investment updatedInvestment = InvestmentsMapper.toEntity(request);

    when(investmentRepository.findById(request.id())).thenReturn(java.util.Optional.of(existingInvestment));
    when(investmentRepository.save(any(Investment.class))).thenReturn(updatedInvestment);

    CustomerServiceRequest serviceRequest = new CustomerServiceRequest();
    serviceRequest.setUserId(request.userId());
    serviceRequest.setRequestType(RequestType.INVESTMENT);
    serviceRequest.setDescription("Customer updated investment to " + request.amount()
            + "$ in " + request.investmentType());
    when(customerRequestService.createRequest(any(CustomerServiceRequest.class))).thenReturn(serviceRequest);

    investmentService.updateInvestment(request);
  }

  @Test
  void testDeleteInvestment() {
    Long id = 1L;
    Long userId = 1L;
    Investment investment = new Investment(id, userId, InvestmentType.STOCKS, 1000.0, new Date());

    when(investmentRepository.findById(id)).thenReturn(java.util.Optional.of(investment));

    assertDoesNotThrow(() -> investmentService.deleteInvestment(id, userId));
  }
}
