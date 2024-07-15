package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.UserAdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserAdvisorySessionServiceImpl implements UserAdvisorySessionService {

  private final RestTemplate restTemplate;

  private final AccountService accountService;

  @Override
  public AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request) {

    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request.setUserId(currentUserId);

    return restTemplate.postForObject(
            "http://localhost:8092/advisory-sessions",
            request,
            AdvisorySessionDTO.class
    );
  }
}
