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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

  private final InvestmentRepository investmentRepository;

  private final RestTemplate restTemplate;

  private final CustomerRequestService customerRequestService;

  @Override
  public InvestmentDTO createInvestment(InvestmentDTO request) {

    BalanceCheckRequest balanceCheckRequest = new BalanceCheckRequest();
    balanceCheckRequest.setAccountId(request.getAccountId());
    balanceCheckRequest.setAmount(request.getAmount());

    try {
      BalanceCheckResponse response = restTemplate.postForObject(
              "http://localhost:8091/check-balance",
              balanceCheckRequest,
              BalanceCheckResponse.class
      );

      if (response == null || !response.isSufficientFunds()) {
        throw new NotSufficientFundsException("Insufficient funds");
      }

      Investment investment = InvestmentsMapper.toEntity(request);
      investment.setDate(new Date());

      CustomerServiceRequest customerServiceRequest = new CustomerServiceRequest();
      customerServiceRequest.setUserId(investment.getUserId());
      customerServiceRequest.setRequestType(RequestType.INVESTMENT);
      customerServiceRequest.setDescription("Customer invested " + request.getAmount()
              + "$ to " + investment.getInvestmentType());
      customerServiceRequest.setStatus(RequestStatus.PENDING);
      customerRequestService.createRequest(customerServiceRequest);

      Investment savedInvestment = investmentRepository.save(investment);

      return InvestmentsMapper.toDto(savedInvestment);
    } catch (RestClientException e) {
      throw new CheckBalanceException("Failed to check balance or create investment", e);
    }
  }

  @Override
  public List<Investment> getAllInvestments(Long userId) {
    return investmentRepository.findAllByUserId(userId);
  }

  @Override
  public Investment updateInvestment(Long id, InvestmentDTO request) {
    Investment investment = investmentRepository.findById(id)
            .orElseThrow(() -> new InvestmentNotFoundException("Investment not found"));

    investment.setInvestmentType(request.getInvestmentType());
    investment.setDate(new Date());
    investment.setAmount(request.getAmount());
    investment.setUserId(request.getUserId());

    Investment updatedInvestment = investmentRepository.save(investment);

    return investmentRepository.save(updatedInvestment);
  }

  @Override
  public void deleteInvestment(Long id) {
    Optional<Investment> investmentOptional = investmentRepository.findById(id);

    if (investmentOptional.isEmpty()){
      throw new InvestmentNotFoundException("Investment not found");
    }

    investmentRepository.deleteById(id);
  }
}
