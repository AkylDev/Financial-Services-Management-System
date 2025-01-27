package kz.projects.ams.services;

import kz.projects.commonlib.dto.AdvisorySessionDTO;

import java.util.List;

public interface UserAdvisorySessionService {
  AdvisorySessionDTO orderAdvisorySession(kz.projects.commonlib.dto.AdvisorySessionDTO request);

  List<AdvisorySessionDTO> getAdvisorySessionsPlanned();

  List<AdvisorySessionDTO> getAdvisersSessions();

  void rescheduleAdvisorySession(Long id, AdvisorySessionDTO request);

  void deleteAdvisorySession(Long id);
}
