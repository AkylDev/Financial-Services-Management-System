package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.UserAdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAdvisorySessionServiceImpl implements UserAdvisorySessionService {

  private final RestTemplate restTemplate;

  private final AccountService accountService;


  @Override
  public AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request) {

    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request.setUserId(currentUserId);

    try {
      return restTemplate.postForObject(
              "http://localhost:8092/advisory-sessions",
              request,
              AdvisorySessionDTO.class
      );
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to order advisory session", e);
    }
  }

  @Override
  public List<AdvisorySessionDTO> getAdvisorySessionsPlanned() {
    Long currentUserId = accountService.getCurrentSessionUser().getId();

    try {
      ResponseEntity<List<AdvisorySessionDTO>> response = restTemplate.exchange(
              "http://localhost:8092/advisory-sessions?userId=" + currentUserId,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<>() {}
      );
      return response.getBody();
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to get advisory sessions", e);
    }
  }

  @Override
  public List<AdvisorySessionDTO> getAdvisersSessions() {
    String email = accountService.getCurrentSessionUser().getEmail();

    try {
      ResponseEntity<List<AdvisorySessionDTO>> response = restTemplate.exchange(
              "http://localhost:8092/advisory-sessions/advisers?email=" + email,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<>() {}
      );
      return response.getBody();
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to get advisory sessions", e);
    }
  }


  @Override
  public void rescheduleAdvisorySession(Long id, AdvisorySessionDTO request) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request.setUserId(currentUserId);
    request.setId(id);

    try {
      restTemplate.put(
              "http://localhost:8092/advisory-sessions",
              request,
              AdvisorySessionDTO.class
      );
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to reschedule advisory session", e);
    }
  }

  @Override
  public void deleteAdvisorySession(Long id) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
    try {
      restTemplate.delete(
              "http://localhost:8092/advisory-sessions/{id}?userId={userId}",
              id, currentUserId
      );
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to delete advisory session", e);
    }
  }
}
