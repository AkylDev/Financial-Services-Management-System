package kz.projects.ams.services;

import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.commonlib.dto.BalanceCheckRequest;
import kz.projects.commonlib.dto.BalanceCheckResponse;

import java.util.List;

public interface UserInvestmentService {

  InvestmentResponse toInvest(InvestmentRequest request);

  void deleteInvestment(Long id);

  List<InvestmentResponse> getAllUsersInvestments();

  BalanceCheckResponse checkBalance(BalanceCheckRequest request);

}
