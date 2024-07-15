package kz.projects.ias.service;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.models.AdvisorySession;

import java.util.List;

public interface AdvisorySessionService {
  AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO advisorySessionDTO);

  List<AdvisorySession> getAdvisorySessions();

  void updateAdvisorySession(AdvisorySessionDTO advisorySessionDTO);

  void deleteAdvisorySession(Long id, Long userId);

}
