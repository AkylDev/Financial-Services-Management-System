package kz.projects.ams.services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.exceptions.InvestmentOperationException;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.models.Account;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.UserInvestmentService;
import kz.projects.ams.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Реализация {@link UserInvestmentService} для управления инвестициями пользователей.
 * Сервис взаимодействует с внешним сервисом для обработки инвестиционных запросов через {@link RestTemplate}.
 */
@Service
@RequiredArgsConstructor
public class UserInvestmentServiceImpl implements UserInvestmentService {

  private final RestTemplate restTemplate;

  private final UserService userService;

  private final TransactionService transactionService;

  private final AccountRepository accountRepository;

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
    Optional<Account> accountOptional = accountRepository.findById(request.accountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found!");
    }

    Long currentUserId = userService.getCurrentSessionUser().getId();
    request = new InvestmentRequest(
            request.id(),
            currentUserId,
            request.accountId(),
            request.investmentType(),
            request.amount()
    );

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(currentUserId)) {
      throw new UnauthorizedException("You are not authorized to change this appointment");
    }

    try {
      InvestmentResponse response = restTemplate.postForObject(
              "http://localhost:8092/investments",
              request,
              InvestmentResponse.class
      );
      if (response == null) {
        throw new InvestmentOperationException("Failed to process investment");
      }

      TransactionRequest transactionRequest = new TransactionRequest(
              request.accountId(),
              request.amount()
      );

      TransactionDTO transactionResponse = transactionService.withdraw(transactionRequest);
      if (transactionResponse == null) {
        throw new InvestmentOperationException("Failed to withdraw amount for investment");
      }

      return response;
    } catch (RestClientException e) {
      throw new InvestmentOperationException("Failed to process investment", e);
    }
  }

  /**
   * Обновляет информацию об инвестиции с указанным идентификатором.
   *
   * @param id идентификатор инвестиции
   * @param request {@link InvestmentRequest} объект, содержащий обновленные данные инвестиции
   * @throws InvestmentOperationException если произошла ошибка при обновлении инвестиции
   */
  @Override
  public void updateInvestment(Long id, InvestmentRequest request) {
    Long currentUserId = userService.getCurrentSessionUser().getId();
    request = new InvestmentRequest(
            id,
            currentUserId,
            request.accountId(),
            request.investmentType(),
            request.amount()
    );

    try {
      restTemplate.put(
              "http://localhost:8092/investments",
              request,
              InvestmentResponse.class
      );
    } catch (RestClientException e) {
      throw new InvestmentOperationException("Failed to update the investment", e);
    }
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
    try {
      restTemplate.delete(
              "http://localhost:8092/investments/{id}?userId={userId}",
              id, currentUserId
      );
    } catch (RestClientException e) {
      throw new InvestmentOperationException("Failed to delete the investment", e);
    }
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

    try {
      ResponseEntity<List<InvestmentResponse>> response = restTemplate.exchange(
              "http://localhost:8092/investments?userId=" + currentUserId,
              HttpMethod.GET,
              null,
              new ParameterizedTypeReference<>() {}
      );
      if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
        throw new InvestmentOperationException("Failed to get investments");
      }
      return response.getBody();
    } catch (RestClientException e) {
      throw new InvestmentOperationException("Failed to get investments", e);
    }
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
    Optional<Account> accountOptional = accountRepository.findById(request.accountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();

    return new BalanceCheckResponse(
            account.getBalance() >= request.amount(),
            account.getBalance()
    );
  }
}
