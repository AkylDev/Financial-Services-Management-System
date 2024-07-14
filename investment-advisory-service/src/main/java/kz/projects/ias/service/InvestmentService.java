package kz.projects.ias.service;

import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.module.Investment;

import java.util.List;

public interface InvestmentService {
  Investment createInvestment(InvestmentDTO investment);

  List<Investment> getAllInvestments();

  Investment updateInvestment(Long id, Investment investment);

  void deleteInvestment(Long id);
}
