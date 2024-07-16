package kz.projects.ias.service;

import kz.projects.ias.dto.FinancialAdvisorDTO;
import kz.projects.ias.models.FinancialAdvisor;

import java.util.List;

public interface FinancialAdvisorService {

  FinancialAdvisor addFinancialAdvisor(FinancialAdvisorDTO financialAdvisor);

  List<FinancialAdvisor> getAllAdvisors();

  void deleteFinancialAdvisor(Long id);

}
