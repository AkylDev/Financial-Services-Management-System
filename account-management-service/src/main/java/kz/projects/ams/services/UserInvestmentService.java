package kz.projects.ams.services;

import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;

import java.util.List;

public interface UserInvestmentService {

  InvestmentResponse toInvest(InvestmentRequest request);

  void deleteInvestment(Long id);

  List<InvestmentResponse> getAllUsersInvestments();

  BalanceCheckResponse checkBalance(BalanceCheckRequest request);

}
