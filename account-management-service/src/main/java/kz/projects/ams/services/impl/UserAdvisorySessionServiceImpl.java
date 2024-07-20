package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.UserAdvisorySessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Реализация {@link UserAdvisorySessionService} для управления консультационными сессиями пользователей.
 * Сервис взаимодействует с внешним сервисом консультационных сессий через {@link RestTemplate}.
 */
@Service
@RequiredArgsConstructor
public class UserAdvisorySessionServiceImpl implements UserAdvisorySessionService {

  private final RestTemplate restTemplate;

  private final AccountService accountService;

  /**
   * Заказывает консультационную сессию для текущего пользователя.
   *
   * @param request {@link AdvisorySessionDTO} объект, содержащий данные для заказа сессии
   * @return {@link AdvisorySessionDTO} объект, представляющий заказанную консультационную сессию
   * @throws AdvisorySessionOrderException если произошла ошибка при заказе сессии
   */
  @Override
  public AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request = new AdvisorySessionDTO(
            request.id(),
            currentUserId,
            request.advisoryId(),
            request.date(),
            request.time()
    );

    try {
      AdvisorySessionDTO response = restTemplate.postForObject(
              "http://localhost:8092/advisory-sessions",
              request,
              AdvisorySessionDTO.class
      );
      if (response == null) {
        throw new AdvisorySessionOrderException("Failed to order advisory session");
      }
      return response;
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to order advisory session", e);
    }
  }

  /**
   * Получает список запланированных консультационных сессий для текущего пользователя.
   *
   * @return список {@link AdvisorySessionDTO} объектов, представляющих запланированные сессии
   * @throws AdvisorySessionOrderException если произошла ошибка при получении сессий
   */
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
      if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
        throw new AdvisorySessionOrderException("Failed to get advisory sessions");
      }
      return response.getBody();
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to get advisory sessions", e);
    }
  }

  /**
   * Получает список консультационных сессий, связанных с консультантами для текущего пользователя.
   *
   * @return список {@link AdvisorySessionDTO} объектов, представляющих сессии с консультантами
   * @throws AdvisorySessionOrderException если произошла ошибка при получении сессий
   */
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
      if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
        throw new AdvisorySessionOrderException("Failed to get advisory sessions");
      }
      return response.getBody();
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to get advisory sessions", e);
    }
  }

  /**
   * Переносит консультационную сессию с указанным идентификатором.
   *
   * @param id идентификатор консультационной сессии
   * @param request {@link AdvisorySessionDTO} объект, содержащий обновленные данные сессии
   * @throws AdvisorySessionOrderException если произошла ошибка при переносе сессии
   */
  @Override
  public void rescheduleAdvisorySession(Long id, AdvisorySessionDTO request) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request = new AdvisorySessionDTO(
            id,
            currentUserId,
            request.advisoryId(),
            request.date(),
            request.time()
    );

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

  /**
   * Удаляет консультационную сессию с указанным идентификатором.
   *
   * @param id идентификатор консультационной сессии
   * @throws AdvisorySessionOrderException если произошла ошибка при удалении сессии
   */
  @Override
  public void deleteAdvisorySession(Long id) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
    try {
      ResponseEntity<Void> response = restTemplate.exchange(
              "http://localhost:8092/advisory-sessions/{id}?userId={userId}",
              HttpMethod.DELETE,
              null,
              Void.class,
              id,
              currentUserId
      );
      if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
        throw new AdvisorySessionOrderException("Failed to delete advisory session");
      }
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to delete advisory session", e);
    }
  }
}
