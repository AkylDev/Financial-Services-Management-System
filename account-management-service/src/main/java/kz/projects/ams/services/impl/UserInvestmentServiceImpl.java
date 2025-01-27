package kz.projects.ams.services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.exceptions.InvestmentOperationException;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.models.Account;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.NotificationEventProducer;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.UserInvestmentService;
import kz.projects.ams.services.UserService;
import kz.projects.commonlib.dto.BalanceCheckRequest;
import kz.projects.commonlib.dto.BalanceCheckResponse;
import kz.projects.commonlib.dto.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Реализация {@link UserInvestmentService} для управления инвестициями пользователей.
 * Сервис взаимодействует с внешним сервисом для обработки инвестиционных запросов через {@link WebClient}.
 */
@Service
@RequiredArgsConstructor
public class UserInvestmentServiceImpl implements UserInvestmentService {

  private final WebClient.Builder webClientBuilder;
  private final UserService userService;
  private final TransactionService transactionService;
  private final AccountRepository accountRepository;
  private final NotificationEventProducer notificationEventProducer;

  private static final String INVESTMENTS_URI = "/investments";
  private static final String TOPIC_NAME = "topic-advisory";

  /**
   * Выполняет инвестицию в указанный счет.
   *
   * @param request {@link InvestmentRequest} объект, содержащий данные инвестиции
   * @return {@link InvestmentResponse} объект, представляющий результат инвестиции
   * @throws UserAccountNotFoundException если указанный счет не найден
   * @throws UnauthorizedException если пользователь не авторизован для выполнения инвестиции
   * @throws InvestmentOperationException если произошла ошибка при обработке инвестиции
   */
  @Override
  public InvestmentResponse toInvest(InvestmentRequest request) {
    Account account = validateAccount(request.accountId());
    validateAccountAccess(account);

    InvestmentRequest investmentRequest = buildInvestmentRequest(request, account.getUser().getId());
    InvestmentResponse response = performInvestment(investmentRequest);

    withdrawForInvestment(request);

    publishEvent("You have successfully invested " + response.amount() + " to " + response.investmentType()
            + " from account with ID " + request.accountId()
            + " at " + response.date());

    return response;
  }

  /**
   * Удаляет инвестицию с указанным идентификатором.
   *
   * @param id идентификатор инвестиции
   * @throws InvestmentOperationException если произошла ошибка при удалении инвестиции
   */
  @Override
  public void deleteInvestment(Long id) {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    deleteUserInvestment(id, currentUserId);
  }

  /**
   * Получает список всех инвестиций текущего пользователя.
   *
   * @return список {@link InvestmentResponse} объектов, представляющих все инвестиции пользователя
   * @throws InvestmentOperationException если произошла ошибка при получении инвестиций
   */
  @Override
  public List<InvestmentResponse> getAllUsersInvestments() {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    return getUserInvestmentList(currentUserId);
  }

  /**
   * Проверяет баланс на указанном счете.
   *
   * @param request {@link BalanceCheckRequest} объект, содержащий данные для проверки баланса
   * @return {@link BalanceCheckResponse} объект, представляющий результат проверки баланса
   * @throws UserAccountNotFoundException если указанный счет не найден
   */
  @Override
  public BalanceCheckResponse checkBalance(BalanceCheckRequest request) {
    Account account = validateAccount(request.accountId());

    return new BalanceCheckResponse(
            account.getBalance() >= request.amount(),
            account.getBalance()
    );
  }

  private Account validateAccount(Long id) {
    return accountRepository.findById(id)
            .orElseThrow(() -> new UserAccountNotFoundException("Account not found"));
  }

  private void validateAccountAccess(Account account) {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    if (!account.getUser().getId().equals(currentUserId)) {
      throw new UnauthorizedException("You are not authorized to access this account");
    }
  }

  private InvestmentRequest buildInvestmentRequest(InvestmentRequest request, Long userId) {
    return new InvestmentRequest(
            request.id(),
            userId,
            request.accountId(),
            request.investmentType(),
            request.amount()
    );
  }

  private InvestmentResponse performInvestment(InvestmentRequest request) {
    try {
      InvestmentResponse response = webClientBuilder.build()
              .post()
              .uri(INVESTMENTS_URI)
              .bodyValue(request)
              .retrieve()
              .bodyToMono(InvestmentResponse.class)
              .block();

      if (response == null) {
        throw new WebClientResponseException("Investment process failed", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "", null, null, null);
      }

      return response;
    } catch (WebClientResponseException e) {
      throw new InvestmentOperationException("Failed to process investment", e);
    }
  }

  private void deleteUserInvestment(Long id, Long currentUserId) {
    try {
      webClientBuilder.build()
              .delete()
              .uri(uriBuilder -> uriBuilder
                      .path(INVESTMENTS_URI + "/{id}")
                      .queryParam("userId", currentUserId)
                      .build(id))
              .retrieve()
              .toBodilessEntity()
              .block();

      publishEvent("You have successfully deleted your investment with ID " + id);
    } catch (WebClientResponseException e) {
      throw new InvestmentOperationException("Failed to delete the investment", e);
    }
  }

  private List<InvestmentResponse> getUserInvestmentList(Long currentUserId) {
    try {
      List<InvestmentResponse> investments = webClientBuilder.build()
              .get()
              .uri(uriBuilder -> uriBuilder
                      .path(INVESTMENTS_URI)
                      .queryParam("userId", currentUserId)
                      .build())
              .retrieve()
              .bodyToFlux(InvestmentResponse.class)
              .collectList()
              .block();

      if (investments == null) {
        throw new InvestmentOperationException("Failed to get investments");
      }

      return investments;
    } catch (WebClientResponseException e) {
      throw new InvestmentOperationException("Failed to get investments", e);
    }
  }

  private void withdrawForInvestment(InvestmentRequest request) {
    TransactionRequest transactionRequest = new TransactionRequest(
            request.accountId(),
            request.amount()
    );

    TransactionDTO transactionResponse = transactionService.withdraw(transactionRequest);
    if (transactionResponse == null) {
      throw new InvestmentOperationException("Failed to withdraw amount for investment");
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

    notificationEventProducer.publishEvent(event, TOPIC_NAME);
  }
}
