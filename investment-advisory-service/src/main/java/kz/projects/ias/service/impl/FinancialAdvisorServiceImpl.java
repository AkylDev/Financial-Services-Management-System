package kz.projects.ias.service.impl;

import kz.projects.ias.dto.FinancialAdvisorDTO;
import kz.projects.ias.exceptions.FinancialAdvisorAlreadyExistsException;
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

  private final FinancialAdvisorRepository financialAdvisorRepository;

  /**
   * Добавляет нового финансового консультанта и сохраняет его в базе данных.
   *
   * @param advisor объект {@link FinancialAdvisorDTO}, содержащий информацию о финансовом консультанте.
   * @return объект {@link FinancialAdvisor}, который был сохранен в базе данных.
   */
  @Override
  public FinancialAdvisor addFinancialAdvisor(FinancialAdvisorDTO advisor) {
    if (financialAdvisorRepository.findByEmail(advisor.email()).isPresent()) {
      throw new FinancialAdvisorAlreadyExistsException("Financial advisor with this email already exists");
    }

    FinancialAdvisor financialAdvisor = new FinancialAdvisor();
    financialAdvisor.setName(advisor.name());
    financialAdvisor.setEmail(advisor.email());
    financialAdvisor.setSpecialization(advisor.specialization());
    return financialAdvisorRepository.save(financialAdvisor);
  }

  /**
   * Возвращает список всех финансовых консультантов.
   *
   * @return список объектов {@link FinancialAdvisor}.
   */
  @Override
  public List<FinancialAdvisor> getAllAdvisors() {
    return financialAdvisorRepository.findAll();
  }

  /**
   * Удаляет финансового консультанта по его идентификатору.
   *
   * @param id идентификатор финансового консультанта, которого нужно удалить.
   * @throws FinancialAdvisorNotFoundException если финансовый консультант с указанным ID не найден.
   */
  @Override
  public void deleteFinancialAdvisor(Long id) {

    Optional<FinancialAdvisor> financialAdvisorOptional = financialAdvisorRepository.findById(id);

    if (financialAdvisorOptional.isEmpty()){
      throw new FinancialAdvisorNotFoundException("Financial Advisor not found");
    }

    financialAdvisorRepository.deleteById(id);
  }
}
