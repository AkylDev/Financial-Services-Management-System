package kz.projects.ams.services.impl;

import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.UserAdvisorySessionService;
import kz.projects.ams.services.UserService;
import kz.projects.commonlib.dto.AdvisorySessionDTO;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
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

  private static final String ADVISORY_SESSIONS_URI = "/advisory-sessions";
  private static final String ADVISERS_SESSIONS_URI = "/advisory-sessions/advisers";
  private static final String TOPIC_NAME = "topic-advisory";

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

    AdvisorySessionDTO sessionRequest = new AdvisorySessionDTO(
            request.id(),
            currentUserId,
            request.advisoryId(),
            request.date(),
            request.time()
    );

    AdvisorySessionDTO response = executeWebClientCall(
            webClientBuilder.build()
                    .post()
                    .uri(ADVISORY_SESSIONS_URI)
                    .bodyValue(sessionRequest),
            AdvisorySessionDTO.class,
            "Failed to order advisory session"
    );

    publishEvent("You have successfully ordered advisory with ID " + response.id() +
            " on " + response.date() + " at " + response.time());
    return response;
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

    return executeWebClientCall(
            webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(ADVISORY_SESSIONS_URI)
                            .queryParam("userId", currentUserId)
                            .build()),
            new ParameterizedTypeReference<>() {
            },
            "Failed to get advisory sessions"
    );
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

    return executeWebClientCall(
            webClientBuilder.build()
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(ADVISERS_SESSIONS_URI)
                            .queryParam("email", email)
                            .build()),
            new ParameterizedTypeReference<>() {
            },
            "Failed to get adviser sessions"
    );
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
    AdvisorySessionDTO sessionRequest = new AdvisorySessionDTO(
            id,
            currentUserId,
            request.advisoryId(),
            request.date(),
            request.time()
    );

    executeWebClientCall(
            webClientBuilder.build()
                    .put()
                    .uri(ADVISORY_SESSIONS_URI)
                    .bodyValue(sessionRequest),
            Void.class,
            "Failed to reschedule advisory session"
    );

    publishEvent("You have successfully rescheduled your advisory session with ID " + id +
            " to " + sessionRequest.date() + " at " + sessionRequest.time());
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

    executeWebClientCall(
            webClientBuilder.build()
                    .delete()
                    .uri(uriBuilder -> uriBuilder
                            .path(ADVISORY_SESSIONS_URI + "/{id}")
                            .queryParam("userId", currentUserId)
                            .build(id)),
            Void.class,
            "Failed to delete advisory session"
    );

    publishEvent("You have successfully deleted your advisory session with ID " + id);
  }

  private void publishEvent(String message) {
    NotificationEvent event = new NotificationEvent(
            userService.getCurrentSessionUser().getId().toString(),
            userService.getCurrentSessionUser().getUsername(),
            userService.getCurrentSessionUser().getEmail(),
            message,
            LocalDateTime.now().toString()
    );

    notificationEventProducer.publishEvent(event, TOPIC_NAME);
  }

  private <T> T executeWebClientCall(WebClient.RequestHeadersSpec<?> request, Class<T> responseType, String errorMessage) {
    try {
      return request.retrieve().bodyToMono(responseType).block();
    } catch (WebClientResponseException e) {
      throw new AdvisorySessionOrderException(errorMessage, e);
    }
  }

  private <T> T executeWebClientCall(WebClient.RequestHeadersSpec<?> request, ParameterizedTypeReference<T> responseType, String errorMessage) {
    try {
      return request.retrieve().bodyToMono(responseType).block();
    } catch (WebClientResponseException e) {
      throw new AdvisorySessionOrderException(errorMessage, e);
    }
  }
}

