package kz.projects.ams.services;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;

import java.util.List;

public interface UserInvestmentAdvisoryService {

  InvestmentResponse toInvest(InvestmentRequest request);

  void updateInvestment(Long id, InvestmentRequest request);

  void deleteInvestment(Long id);

  List<InvestmentResponse> getAllUsersInvestments();

  BalanceCheckResponse checkBalance(BalanceCheckRequest request);

  AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request);

  List<AdvisorySessionDTO> getAdvisorySessionsPlanned();

  void rescheduleAdvisorySession(Long id, AdvisorySessionDTO request);

  void deleteAdvisorySession(Long id);

}
