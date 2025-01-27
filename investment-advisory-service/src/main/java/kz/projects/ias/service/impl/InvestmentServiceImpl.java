package kz.projects.ias.service.impl;

import kz.projects.ias.dto.BalanceCheckRequest;
import kz.projects.ias.dto.BalanceCheckResponse;
import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.exceptions.CheckBalanceException;
import kz.projects.ias.exceptions.InvestmentNotFoundException;
import kz.projects.ias.exceptions.NotSufficientFundsException;
import kz.projects.ias.mapper.InvestmentsMapper;
import kz.projects.ias.models.CustomerServiceRequest;
import kz.projects.ias.models.Investment;
import kz.projects.ias.models.enums.RequestStatus;
import kz.projects.ias.models.enums.RequestType;
import kz.projects.ias.repositories.InvestmentRepository;
import kz.projects.ias.service.CustomerRequestService;
import kz.projects.ias.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvestmentServiceImpl implements InvestmentService {

  private final InvestmentRepository investmentRepository;
  private final WebClient.Builder webClientBuilder;
  private final CustomerRequestService customerRequestService;

  private static final String CHECK_BALANCE_URI = "/api/v1/ams/check-balance";

  /**
   * Creates an investment, checking for sufficient funds first.
   *
   * @param request InvestmentDTO containing the investment data.
   * @return The saved InvestmentDTO.
   * @throws NotSufficientFundsException if the account balance is insufficient.
   * @throws CheckBalanceException       if an error occurs during balance checking or investment creation.
   */
  @Override
  public InvestmentDTO createInvestment(InvestmentDTO request) {
    BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest(request.accountId(), request.amount());

    BalanceCheckResponse response = checkBalance(balanceCheckRequest);

    if (response == null || !response.sufficientFunds()) {
      throw new NotSufficientFundsException("Insufficient funds");
    }

    Investment investment = InvestmentsMapper.toEntity(request);
    investment.setDate(new Date());

    CustomerServiceRequest serviceRequest = createCustomerInvestmentRequest(request);
    serviceRequest.setStatus(RequestStatus.PENDING);
    customerRequestService.createRequest(serviceRequest);

    Investment savedInvestment = investmentRepository.save(investment);

    return InvestmentsMapper.toDto(savedInvestment);
  }

  /**
   * Returns all investments for a specific user.
   *
   * @param userId The user ID.
   * @return A list of Investment entities.
   */
  @Transactional(readOnly = true)
  @Override
  public List<Investment> getAllInvestments(Long userId) {
    return investmentRepository.findAllByUserId(userId);
  }

  /**
   * Updates an existing investment.
   *
   * @param request InvestmentDTO with updated data.
   * @throws InvestmentNotFoundException if the investment is not found.
   * @throws IllegalArgumentException    if the user does not have permission to update the investment.
   */
  @Override
  public void updateInvestment(InvestmentDTO request) {
    Investment investment = investmentRepository.findById(request.id())
            .orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));

    if (!investment.getUserId().equals(request.userId())) {
      throw new IllegalArgumentException("You are not allowed to update this investment");
    }

    investment.setInvestmentType(request.investmentType());
    investment.setDate(new Date());
    investment.setAmount(request.amount());
    investment.setUserId(request.userId());

    CustomerServiceRequest serviceRequest = createCustomerInvestmentRequest(request);
    serviceRequest.setStatus(RequestStatus.CHANGED);
    customerRequestService.createRequest(serviceRequest);

    investmentRepository.save(investment);
  }

  /**
   * Deletes an investment and creates a cancellation request.
   *
   * @param id     The investment ID to delete.
   * @param userId The user requesting the deletion.
   * @throws InvestmentNotFoundException if the investment is not found.
   * @throws IllegalArgumentException    if the user does not have permission to delete the investment.
   */
  @Override
  public void deleteInvestment(Long id, Long userId) {
    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));

    if (!investment.getUserId().equals(userId)) {
      throw new IllegalArgumentException("You are not allowed to delete this investment");
    }

    InvestmentDTO request = InvestmentsMapper.toDto(investment);

    CustomerServiceRequest serviceRequest = createCustomerInvestmentRequest(request);
    serviceRequest.setStatus(RequestStatus.CANCELLED);
    customerRequestService.createRequest(serviceRequest);

    investmentRepository.deleteById(id);
  }

  private CustomerServiceRequest createCustomerInvestmentRequest(InvestmentDTO investment) {
    CustomerServiceRequest serviceRequest = new CustomerServiceRequest();
    serviceRequest.setUserId(investment.userId());
    serviceRequest.setRequestType(RequestType.INVESTMENT);
    serviceRequest.setDescription("Customer invested " + investment.amount() + " to " + investment.investmentType());
    return serviceRequest;
  }

  private BalanceCheckResponse checkBalance(BalanceCheckRequest request) {
    try {
      return webClientBuilder.build()
              .post()
              .uri(CHECK_BALANCE_URI)
              .bodyValue(request)
              .retrieve()
              .bodyToMono(BalanceCheckResponse.class)
              .block();
    } catch (WebClientResponseException e) {
      throw new CheckBalanceException("Failed to check balance or create investment", e);
    }
  }
}

