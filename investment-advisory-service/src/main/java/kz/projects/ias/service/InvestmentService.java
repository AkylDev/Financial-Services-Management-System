package kz.projects.ias.service;

import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.models.Investment;

import java.util.List;

public interface InvestmentService {
  InvestmentDTO createInvestment(InvestmentDTO investment);

  List<Investment> getAllInvestments(Long userId);

  void updateInvestment(InvestmentDTO investment);

  void deleteInvestment(Long id, Long userId);
}
