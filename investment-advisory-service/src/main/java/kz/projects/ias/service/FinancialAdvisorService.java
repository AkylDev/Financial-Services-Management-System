package kz.projects.ias.service;

import kz.projects.ias.models.FinancialAdvisor;

public interface FinancialAdvisorService {

  FinancialAdvisor addFinancialAdvisor(FinancialAdvisor financialAdvisor);

  void deleteFinancialAdvisor(Long id);

}
