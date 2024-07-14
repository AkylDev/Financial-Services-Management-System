package kz.projects.ias.service.impl;

import kz.projects.ias.dto.BalanceCheckRequest;
import kz.projects.ias.dto.BalanceCheckResponse;
import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.exceptions.NotSufficientFundsException;
import kz.projects.ias.module.Investment;
import kz.projects.ias.repositories.InvestmentRepository;
import kz.projects.ias.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

  private final InvestmentRepository investmentRepository;

  private final RestTemplate restTemplate;

  @Override
  public Investment createInvestment(InvestmentDTO investmentDTO) {

    BalanceCheckRequest request = new BalanceCheckRequest();
    request.setAccountId(investmentDTO.getAccountId());
    request.setAmount(investmentDTO.getAmount());

    BalanceCheckResponse response = restTemplate.postForObject(
            "http://localhost:8091/check-balance",
            request,
            BalanceCheckResponse.class
    );

    assert response != null;
    if (!response.isSufficientFunds()){
      throw new NotSufficientFundsException("Insufficient funds");
    }
    Investment investment = new Investment();

    investment.setAmount(investmentDTO.getAmount());
    investment.setInvestmentType(investmentDTO.getInvestmentType());
    investment.setUserId(investmentDTO.getUserId());
    investment.setDate(new Date());

    return investmentRepository.save(investment);
  }
}
