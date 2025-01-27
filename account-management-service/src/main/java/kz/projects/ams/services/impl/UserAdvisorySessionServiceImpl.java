package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.UserAdvisorySessionService;
import kz.projects.ams.services.UserService;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация {@link UserAdvisorySessionService} для управления консультационными сессиями пользователей.
 * Сервис взаимодействует с внешним сервисом консультационных сессий через {@link WebClient}.
 */
@Service
@RequiredArgsConstructor
public class UserAdvisorySessionServiceImpl implements UserAdvisorySessionService {

  private final WebClient.Builder webClientBuilder;

  private final UserService userService;

  private final NotificationEventProducer notificationEventProducer;

  /**
   * Заказывает консультационную сессию для текущего пользователя.
   *
   * @param request {@link AdvisorySessionDTO} объект, содержащий данные для заказа сессии
   * @return {@link AdvisorySessionDTO} объект, представляющий заказанную консультационную сессию
   * @throws AdvisorySessionOrderException если произошла ошибка при заказе сессии
   */
  @Override
  public AdvisorySessionDTO orderAdvisorySession(AdvisorySessionDTO request) {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    request = new AdvisorySessionDTO(
            request.id(),
            currentUserId,
            request.advisoryId(),
            request.date(),
            request.time()
    );

    try {
      AdvisorySessionDTO response = webClientBuilder.build()
              .post()
              .uri("/advisory-sessions")
              .bodyValue(request)
              .retrieve()
              .bodyToMono(AdvisorySessionDTO.class)
              .block();
      if (response == null) {
        throw new AdvisorySessionOrderException("Failed to order advisory session");
      }

      publishEvent("You have successfully ordered advisory with ID " + response.id() +
              " on " + request.date() + " at " + request.time());
      return response;
    } catch (WebClientResponseException e) {
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
    Long currentUserId = userService.getCurrentSessionUser().getId();

    try {
      List<AdvisorySessionDTO> advisorySessions = webClientBuilder.build()
              .get()
              .uri(uriBuilder -> uriBuilder
                      .path("/advisory-sessions")
                      .queryParam("userId", currentUserId)
                      .build())
              .retrieve()
              .bodyToFlux(AdvisorySessionDTO.class)
              .collectList()
              .block();

      if (advisorySessions == null) {
        throw new AdvisorySessionOrderException("Failed to get advisory sessions");
      }

      return advisorySessions;
    } catch (WebClientResponseException e) {
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
    String email = userService.getCurrentSessionUser().getEmail();
    try {
      List<AdvisorySessionDTO> response = webClientBuilder.build()
              .get()
              .uri(uriBuilder -> uriBuilder
                      .path("/advisory-sessions/advisers")
                      .queryParam("email", email)
                      .build()
              )
              .retrieve()
              .bodyToFlux(AdvisorySessionDTO.class)
              .collectList()
              .block();

      if (response == null || response.isEmpty()) {
        throw new AdvisorySessionOrderException("Failed to get advisory sessions");
      }
      return response;
    } catch (WebClientResponseException e) {
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
    Long currentUserId = userService.getCurrentSessionUser().getId();
    request = new AdvisorySessionDTO(
            id,
            currentUserId,
            request.advisoryId(),
            request.date(),
            request.time()
    );

    try {

      webClientBuilder.build()
              .put()
              .uri("/advisory-sessions")
              .bodyValue(request)
              .retrieve()
              .bodyToMono(AdvisorySessionDTO.class)
              .block();

      publishEvent("You have successfully rescheduled your advisory session with ID " + request.id() +
              " to " + request.date() + " at " + request.time());
    } catch (WebClientResponseException e) {
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
    Long currentUserId = userService.getCurrentSessionUser().getId();
    try {
      webClientBuilder.build()
              .delete()
              .uri(uriBuilder -> uriBuilder
                      .path("/advisory-sessions/{id}")
                      .queryParam("userId", currentUserId)
                      .build(id)
              )
              .retrieve()
              .toBodilessEntity()
              .block();
      publishEvent("You have successfully deleted your advisory session with ID " + id);
    } catch (WebClientResponseException e) {
      throw new AdvisorySessionOrderException("Failed to delete advisory session", e);
    }
  }

  private void publishEvent(String message) {
    NotificationEvent event = new NotificationEvent(
            userService.getCurrentSessionUser().getId().toString(),
            userService.getCurrentSessionUser().getUsername(),
            userService.getCurrentSessionUser().getEmail(),
            message,
            LocalDateTime.now().toString()
    );

    notificationEventProducer.publishEvent(event, "topic-account");
  }
}
