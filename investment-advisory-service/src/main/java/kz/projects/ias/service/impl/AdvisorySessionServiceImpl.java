package kz.projects.ias.service.impl;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.exceptions.FinancialAdvisorNotFoundException;
import kz.projects.ias.mapper.AdvisorySessionMapper;
import kz.projects.ias.module.AdvisorySession;
import kz.projects.ias.module.FinancialAdvisor;
import kz.projects.ias.module.enums.RequestStatus;
import kz.projects.ias.repositories.AdvisorySessionRepository;
import kz.projects.ias.repositories.FinancialAdvisorRepository;
import kz.projects.ias.service.AdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvisorySessionServiceImpl implements AdvisorySessionService {

  private final FinancialAdvisorRepository financialAdvisorRepository;

  private final AdvisorySessionMapper advisorySessionMapper;

  private final AdvisorySessionRepository advisorySessionRepository;

  @Override
  public AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO request) {

    Optional<FinancialAdvisor> advisorOptional = financialAdvisorRepository.findById(request.getAdvisoryId());

    if (advisorOptional.isEmpty()){
      throw new FinancialAdvisorNotFoundException("Financial Advisor Not Found!");
    }

    FinancialAdvisor advisor = advisorOptional.get();

    AdvisorySession advisorySession = new AdvisorySession();
    advisorySession.setUserId(request.getUserId());
    advisorySession.setFinancialAdvisor(advisor);
    advisorySession.setDate(request.getDate());
    advisorySession.setTime(request.getTime());
    advisorySession.setStatus(RequestStatus.PENDING);

    return advisorySessionMapper.toDto(advisorySessionRepository.save(advisorySession));
  }
}
