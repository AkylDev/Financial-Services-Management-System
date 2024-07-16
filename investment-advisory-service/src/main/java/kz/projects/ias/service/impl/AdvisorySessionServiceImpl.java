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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvisorySessionServiceImpl implements AdvisorySessionService {

  private final FinancialAdvisorRepository financialAdvisorRepository;

  private final AdvisorySessionRepository advisorySessionRepository;

  private final CustomerRequestService customerRequestService;

  private CustomerServiceRequest customerAdvisorySessionRequest(AdvisorySessionDTO request, String advisorName){
    CustomerServiceRequest customerServiceRequest = new CustomerServiceRequest();
    customerServiceRequest.setUserId(request.getUserId());
    customerServiceRequest.setRequestType(RequestType.ADVISORY);
    customerServiceRequest.setDescription("Customer set up advisory session with " +
            advisorName + " on " + request.getDate() +
            " at " + request.getTime());

    return customerServiceRequest;
  }

  @Override
  public AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO request) {

    FinancialAdvisor advisor = financialAdvisorRepository.findById(request.getAdvisoryId())
            .orElseThrow(() -> new FinancialAdvisorNotFoundException("Advisor not found"));

    AdvisorySession session = AdvisorySessionMapper.toEntity(request, advisor);

    CustomerServiceRequest serviceRequest = customerAdvisorySessionRequest(request, session.getFinancialAdvisor().getName());
    serviceRequest.setStatus(RequestStatus.PENDING);
    customerRequestService.createRequest(serviceRequest);

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
  public List<AdvisorySessionDTO> getFinancialAdviserSessions(String email) {
    FinancialAdvisor advisor = financialAdvisorRepository.findByEmail(email)
            .orElseThrow(() -> new FinancialAdvisorNotFoundException("Financial adviser not found"));

    List<AdvisorySession> sessions = advisorySessionRepository.findAllByFinancialAdvisor(advisor);
    return sessions.stream()
            .map(AdvisorySessionMapper::toDto)
            .collect(Collectors.toList());
  }

  @Override
  public void updateAdvisorySession(AdvisorySessionDTO request) {

    AdvisorySession session = advisorySessionRepository.findById(request.getId())
                    .orElseThrow(() -> new AdvisorySessionNotFoundException("AdvisorySession with this ID not found"));

    if (!session.getUserId().equals(request.getUserId())){
      throw new IllegalArgumentException("You are not allowed");
    }

    session.setUserId(request.getUserId());
    session.setDate(request.getDate());
    session.setTime(request.getTime());
    session.setStatus(RequestStatus.RESCHEDULED);

    CustomerServiceRequest serviceRequest = customerAdvisorySessionRequest(request, session.getFinancialAdvisor().getName());
    serviceRequest.setStatus(RequestStatus.RESCHEDULED);
    customerRequestService.createRequest(serviceRequest);

    advisorySessionRepository.save(session);
  }

  @Override
  public void deleteAdvisorySession(Long id, Long userId) {
    AdvisorySession session = advisorySessionRepository.findById(id)
            .orElseThrow(() -> new AdvisorySessionNotFoundException("AdvisorySession with this ID not found"));

    if (!session.getUserId().equals(userId)) {
      throw new IllegalArgumentException("You are not allowed");
    }

    CustomerServiceRequest serviceRequest = customerAdvisorySessionRequest(AdvisorySessionMapper.toDto(session), session.getFinancialAdvisor().getName());
    serviceRequest.setStatus(RequestStatus.CANCELLED);
    customerRequestService.createRequest(serviceRequest);

    advisorySessionRepository.deleteById(id);
  }
}
