package kz.projects.ams.services;

import kz.projects.ams.dto.AdvisorySessionDTO;

public interface UserAdvisorySessionService {
  AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request);
}
