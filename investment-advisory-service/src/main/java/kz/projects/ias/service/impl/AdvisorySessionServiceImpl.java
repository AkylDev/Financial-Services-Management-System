package kz.projects.ias.service.impl;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.exceptions.AdvisorySessionNotFoundException;
import kz.projects.ias.exceptions.FinancialAdvisorNotFoundException;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisorySessionServiceImpl implements AdvisorySessionService {

  private final FinancialAdvisorRepository financialAdvisorRepository;

  private final AdvisorySessionRepository advisorySessionRepository;

  private final CustomerRequestService customerRequestService;

  @Override
  public AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO request) {

    FinancialAdvisor advisor = financialAdvisorRepository.findById(request.getAdvisoryId())
            .orElseThrow(() -> new FinancialAdvisorNotFoundException("Advisor not found"));

    AdvisorySession session = AdvisorySessionMapper.toEntity(request, advisor);

    CustomerServiceRequest customerServiceRequest = new CustomerServiceRequest();
    customerServiceRequest.setUserId(request.getUserId());
    customerServiceRequest.setRequestType(RequestType.INVESTMENT);
    customerServiceRequest.setDescription("Customer set up advisory session with " +
            session.getFinancialAdvisor().getName() + " on " + request.getDate() +
            " at " + request.getTime());
    customerServiceRequest.setStatus(RequestStatus.PENDING);
    customerRequestService.createRequest(customerServiceRequest);

    AdvisorySession savedSession = advisorySessionRepository.save(session);

    return AdvisorySessionMapper.toDto(savedSession);
  }

  @Override
  public List<AdvisorySessionDTO> getAdvisorySessions(Long userId) {
    List<AdvisorySession> sessions = advisorySessionRepository.findAllByUserId(userId);
    return sessions.stream()
            .map(AdvisorySessionMapper::toDto)
            .collect(Collectors.toList());
  }

  @Override
  public void updateAdvisorySession(AdvisorySessionDTO request) {

    Optional<AdvisorySession> advisorySessionOptional = advisorySessionRepository.findById(request.getId());

    if (advisorySessionOptional.isEmpty()){
      throw new AdvisorySessionNotFoundException("AdvisorySession with this ID not found");
    }

    AdvisorySession advisorySession = advisorySessionOptional.get();

    if (!advisorySession.getUserId().equals(request.getUserId())){
      throw new IllegalArgumentException("You are not allowed");
    }

    advisorySession.setUserId(request.getUserId());
    advisorySession.setDate(request.getDate());
    advisorySession.setTime(request.getTime());
    advisorySession.setStatus(RequestStatus.RESCHEDULED);

    advisorySessionRepository.save(advisorySession);
  }

  @Override
  public void deleteAdvisorySession(Long id, Long userId) {
    Optional<AdvisorySession> advisorySessionOptional = advisorySessionRepository.findById(id);

    if (advisorySessionOptional.isEmpty()) {
      throw new AdvisorySessionNotFoundException("Advisory session with this ID not found");
    }

    AdvisorySession advisorySession = advisorySessionOptional.get();

    if (!advisorySession.getUserId().equals(userId)) {
      throw new IllegalArgumentException("You are not allowed");
    }

    advisorySessionRepository.deleteById(id);
  }
}
