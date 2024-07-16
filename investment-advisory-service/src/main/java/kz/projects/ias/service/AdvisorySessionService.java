package kz.projects.ias.service;

import kz.projects.ias.dto.AdvisorySessionDTO;

import java.util.List;

public interface AdvisorySessionService {
  AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO advisorySessionDTO);

  List<AdvisorySessionDTO> getAdvisorySessions(Long userId);
  List<AdvisorySessionDTO> getFinancialAdviserSessions(String email);

  void updateAdvisorySession(AdvisorySessionDTO advisorySessionDTO);

  void deleteAdvisorySession(Long id, Long userId);

}
