package kz.projects.ams.services;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;

public interface UserInvestmentAdvisoryService {

  InvestmentResponse toInvest(InvestmentRequest request);

  BalanceCheckResponse checkBalance(BalanceCheckRequest request);

  AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request);

  void rescheduleAdvisorySession(Long id, AdvisorySessionDTO request);

  void deleteAdvisorySession(Long id);

}
