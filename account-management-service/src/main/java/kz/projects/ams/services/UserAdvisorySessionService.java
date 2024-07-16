package kz.projects.ams.services;

import kz.projects.ams.dto.AdvisorySessionDTO;

import java.util.List;

public interface UserAdvisorySessionService {
  AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request);

  List<AdvisorySessionDTO> getAdvisorySessionsPlanned();

  void rescheduleAdvisorySession(Long id, AdvisorySessionDTO request);

  void deleteAdvisorySession(Long id);
}
