package kz.projects.ias.service;

import kz.projects.ias.dto.AdvisorySessionDTO;
import kz.projects.ias.module.AdvisorySession;

import java.util.List;

public interface AdvisorySessionService {
  AdvisorySessionDTO createAdvisorySession(AdvisorySessionDTO advisorySessionDTO);

  List<AdvisorySession> getAdvisorySessions();

  void updateAdvisorySession(AdvisorySessionDTO advisorySessionDTO);

}
