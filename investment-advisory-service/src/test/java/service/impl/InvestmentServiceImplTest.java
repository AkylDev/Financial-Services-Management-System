package service.impl;

import kz.projects.ias.dto.BalanceCheckRequest;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class InvestmentServiceImplTest {

  @Mock
  private InvestmentRepository investmentRepository;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private CustomerRequestService customerRequestService;

  @InjectMocks
  private InvestmentServiceImpl investmentService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void testCreateInvestment_Success() {
    BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse();
    balanceCheckResponse.setSufficientFunds(true);
    balanceCheckResponse.setCurrentBalance(1000.0);

    when(restTemplate.postForObject(anyString(), any(BalanceCheckRequest.class), eq(BalanceCheckResponse.class)))
            .thenReturn(balanceCheckResponse);

    InvestmentDTO request = new InvestmentDTO();
    request.setAccountId(1L);
    request.setUserId(1L);
    request.setInvestmentType(InvestmentType.STOCKS);
    request.setAmount(500.0);

    Investment investment = InvestmentsMapper.toEntity(request);
    investment.setId(1L);

    when(investmentRepository.save(any(Investment.class))).thenReturn(investment);

    CustomerServiceRequest serviceRequest = new CustomerServiceRequest();
    serviceRequest.setUserId(request.getUserId());
    serviceRequest.setRequestType(RequestType.INVESTMENT);
    serviceRequest.setDescription("Customer invested " + request.getAmount()
            + "$ to " + request.getInvestmentType());
    when(customerRequestService.createRequest(any(CustomerServiceRequest.class))).thenReturn(serviceRequest);

    InvestmentDTO result = investmentService.createInvestment(request);

    assertNotNull(result);
    assertEquals(investment.getId(), result.getId());
    assertEquals(investment.getUserId(), result.getUserId());
    assertEquals(investment.getInvestmentType(), result.getInvestmentType());
    assertEquals(investment.getAmount(), result.getAmount());
  }

  @Test
  void testCreateInvestment_InsufficientFunds() {
    BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse();
    balanceCheckResponse.setSufficientFunds(false);

    when(restTemplate.postForObject(anyString(), any(BalanceCheckRequest.class), eq(BalanceCheckResponse.class)))
            .thenReturn(balanceCheckResponse);

    InvestmentDTO request = new InvestmentDTO();
    request.setAccountId(1L);
    request.setUserId(1L);
    request.setInvestmentType(InvestmentType.STOCKS);
    request.setAmount(1500.0);

    assertThrows(NotSufficientFundsException.class, () -> investmentService.createInvestment(request));
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
    InvestmentDTO request = new InvestmentDTO();
    request.setId(1L);
    request.setUserId(1L);
    request.setInvestmentType(InvestmentType.STOCKS);
    request.setAmount(800.0);

    Investment existingInvestment = new Investment(1L, 1L, InvestmentType.STOCKS, 500.0, new Date());
    Investment updatedInvestment = InvestmentsMapper.toEntity(request);

    when(investmentRepository.findById(request.getId())).thenReturn(java.util.Optional.of(existingInvestment));
    when(investmentRepository.save(any(Investment.class))).thenReturn(updatedInvestment);

    CustomerServiceRequest serviceRequest = new CustomerServiceRequest();
    serviceRequest.setUserId(request.getUserId());
    serviceRequest.setRequestType(RequestType.INVESTMENT);
    serviceRequest.setDescription("Customer updated investment to " + request.getAmount()
            + "$ in " + request.getInvestmentType());
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
