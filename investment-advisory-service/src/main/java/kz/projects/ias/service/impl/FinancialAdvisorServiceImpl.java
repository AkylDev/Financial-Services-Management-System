package kz.projects.ias.service.impl;

import kz.projects.ias.exceptions.FinancialAdvisorNotFoundException;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.repositories.FinancialAdvisorRepository;
import kz.projects.ias.service.FinancialAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialAdvisorServiceImpl implements FinancialAdvisorService {

  private final FinancialAdvisorRepository  financialAdvisorRepository;

  @Override
  public FinancialAdvisor addFinancialAdvisor(FinancialAdvisor advisor) {
    return financialAdvisorRepository.save(advisor);
  }

  @Override
  public void deleteFinancialAdvisor(Long id) {

    Optional<FinancialAdvisor> financialAdvisorOptional = financialAdvisorRepository.findById(id);

    if (financialAdvisorOptional.isEmpty()){
      throw new FinancialAdvisorNotFoundException("Financial Advisor not found");
    }

    financialAdvisorRepository.deleteById(id);
  }


}
