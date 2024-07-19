package kz.projects.ias.service.impl;

import kz.projects.ias.dto.FinancialAdvisorDTO;
import kz.projects.ias.exceptions.FinancialAdvisorNotFoundException;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.repositories.FinancialAdvisorRepository;
import kz.projects.ias.service.FinancialAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FinancialAdvisorServiceImpl implements FinancialAdvisorService {

  private final FinancialAdvisorRepository  financialAdvisorRepository;

  @Override
  public FinancialAdvisor addFinancialAdvisor(FinancialAdvisorDTO advisor) {
    FinancialAdvisor financialAdvisor = new FinancialAdvisor();
    financialAdvisor.setName(advisor.name());
    financialAdvisor.setEmail(advisor.email());
    financialAdvisor.setSpecialization(advisor.specialization());
    return financialAdvisorRepository.save(financialAdvisor);
  }

  @Override
  public List<FinancialAdvisor> getAllAdvisors() {
    return financialAdvisorRepository.findAll();
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
