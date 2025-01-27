package kz.projects.ias.service.impl;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.exceptions.AdvisorySessionNotFoundException;
import kz.projects.ias.exceptions.FinancialAdvisorNotFoundException;
import kz.projects.ias.exceptions.UnauthorizedAccessException;
import kz.projects.ias.mapper.AdvisorySessionMapper;
import kz.projects.ias.models.AdvisorySession;
import kz.projects.ias.models.CustomerServiceRequest;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.models.enums.RequestStatus;
import kz.projects.ias.models.enums.RequestType;
import kz.projects.ias.repositories.AdvisorySessionRepository;
import kz.projects.ias.repositories.FinancialAdvisorRepository;
import kz.projects.ias.service.AdvisorySessionService;
import kz.projects.ias.service.CustomerRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdvisorySessionServiceImpl implements AdvisorySessionService {

  private final FinancialAdvisorRepository financialAdvisorRepository;
  private final AdvisorySessionRepository advisorySessionRepository;
  private final CustomerRequestService customerRequestService;

  /**
   * Создает новую консультацию и сохраняет ее в базе данных.
   * Также создает запрос на обслуживание для консультации.
   *
   * @param request объект {@link AdvisorySessionDTO} с информацией о консультации.
   * @return объект {@link AdvisorySessionDTO} с информацией о сохраненной консультации.
   * @throws FinancialAdvisorNotFoundException если финансовый консультант не найден.
   */
  @Override
  public AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO request) {

    FinancialAdvisor advisor = financialAdvisorRepository.findById(request.advisoryId())
            .orElseThrow(() -> new FinancialAdvisorNotFoundException("Advisor not found"));

    AdvisorySession session = AdvisorySessionMapper.toEntity(request, advisor);

    CustomerServiceRequest serviceRequest = customerAdvisorySessionRequest(request, session.getFinancialAdvisor().getName());
    serviceRequest.setStatus(RequestStatus.PENDING);
    customerRequestService.createRequest(serviceRequest);

    AdvisorySession savedSession = advisorySessionRepository.save(session);

    return AdvisorySessionMapper.toDto(savedSession);
  }

  /**
   * Возвращает список всех консультаций для указанного пользователя.
   *
   * @param userId идентификатор пользователя.
   * @return список объектов {@link AdvisorySessionDTO} с консультациями пользователя.
   */
  @Transactional(readOnly = true)
  @Override
  public List<AdvisorySessionDTO> getAdvisorySessions(Long userId) {
    List<AdvisorySession> sessions = advisorySessionRepository.findAllByUserId(userId);
    return sessions.stream()
            .map(AdvisorySessionMapper::toDto)
            .collect(Collectors.toList());
  }

  /**
   * Возвращает список всех консультаций для указанного финансового консультанта.
   *
   * @param email адрес электронной почты финансового консультанта.
   * @return список объектов {@link AdvisorySessionDTO} с консультациями консультанта.
   * @throws FinancialAdvisorNotFoundException если финансовый консультант не найден.
   */
  @Transactional(readOnly = true)
  @Override
  public List<AdvisorySessionDTO> getFinancialAdviserSessions(String email) {
    FinancialAdvisor advisor = financialAdvisorRepository.findByEmail(email)
            .orElseThrow(() -> new FinancialAdvisorNotFoundException("Financial adviser not found"));

    List<AdvisorySession> sessions = advisorySessionRepository.findAllByFinancialAdvisor(advisor);
    return sessions.stream()
            .map(AdvisorySessionMapper::toDto)
            .collect(Collectors.toList());
  }

  /**
   * Обновляет информацию о консультации.
   * Также создает запрос на обслуживание для обновленной консультации.
   *
   * @param request объект {@link AdvisorySessionDTO} с обновленной информацией о консультации.
   * @throws AdvisorySessionNotFoundException если консультация с указанным ID не найдена.
   * @throws UnauthorizedAccessException      если пользователь не имеет прав на обновление консультации.
   */
  @Override
  public void updateAdvisorySession(AdvisorySessionDTO request) {

    AdvisorySession session = getValidatedAdvisorySession(request.id());
    validateUserAccess(session, request.userId());

    session.setUserId(request.userId());
    session.setDate(request.date());
    session.setTime(request.time());
    session.setStatus(RequestStatus.RESCHEDULED);

    CustomerServiceRequest serviceRequest = customerAdvisorySessionRequest(request, session.getFinancialAdvisor().getName());
    serviceRequest.setStatus(RequestStatus.RESCHEDULED);
    customerRequestService.createRequest(serviceRequest);

    advisorySessionRepository.save(session);
  }

  /**
   * Удаляет консультацию по-указанному ID и создает запрос на обслуживание для удаления.
   *
   * @param id     идентификатор консультации.
   * @param userId идентификатор пользователя, инициировавшего удаление.
   * @throws AdvisorySessionNotFoundException если консультация с указанным ID не найдена.
   * @throws UnauthorizedAccessException      если пользователь не имеет прав на удаление консультации.
   */
  @Override
  public void deleteAdvisorySession(Long id, Long userId) {
    AdvisorySession session = getValidatedAdvisorySession(id);
    validateUserAccess(session, userId);

    CustomerServiceRequest serviceRequest = customerAdvisorySessionRequest(AdvisorySessionMapper.toDto(session),
            session.getFinancialAdvisor().getName());
    serviceRequest.setStatus(RequestStatus.CANCELLED);
    customerRequestService.createRequest(serviceRequest);

    advisorySessionRepository.deleteById(id);
  }

  private AdvisorySession getValidatedAdvisorySession(Long id) {
    return advisorySessionRepository.findById(id)
            .orElseThrow(() -> new AdvisorySessionNotFoundException("AdvisorySession with this ID not found"));
  }

  private void validateUserAccess(AdvisorySession session, Long userId) {
    if (!session.getUserId().equals(userId)) {
      throw new UnauthorizedAccessException("You are not allowed");
    }
  }

  private CustomerServiceRequest customerAdvisorySessionRequest(AdvisorySessionDTO request, String advisorName) {
    CustomerServiceRequest customerServiceRequest = new CustomerServiceRequest();
    customerServiceRequest.setUserId(request.userId());
    customerServiceRequest.setRequestType(RequestType.ADVISORY);
    customerServiceRequest.setDescription("Customer set up advisory session with " +
            advisorName + " on " + request.date() +
            " at " + request.time());

    return customerServiceRequest;
  }
}
