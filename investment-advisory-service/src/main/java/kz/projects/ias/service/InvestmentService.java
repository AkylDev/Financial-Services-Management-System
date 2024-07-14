package kz.projects.ias.service;

import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.module.Investment;

public interface InvestmentService {
  Investment createInvestment(InvestmentDTO investment);
}
