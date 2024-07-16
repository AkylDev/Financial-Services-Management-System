package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.exceptions.AdvisorySessionOrderException;
import kz.projects.ams.exceptions.InvestmentOperationException;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.models.Account;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.TransactionService;
import kz.projects.ams.services.UserInvestmentAdvisoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInvestmentAdvisoryServiceImpl implements UserInvestmentAdvisoryService {

  private final RestTemplate restTemplate;

  private final AccountService accountService;

  private final TransactionService transactionService;

  private final AccountRepository accountRepository;

  @Override
  public InvestmentResponse toInvest(InvestmentRequest request){
    Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found!");
    }

    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request.setUserId(currentUserId);

    Account account = accountOptional.get();
    if (!account.getUser().getId().equals(currentUserId)){
      throw new UnauthorizedException("You are not authorized to change this appointment");
    }

    try {
      InvestmentResponse response = restTemplate.postForObject(
              "http://localhost:8092/investments",
              request,
              InvestmentResponse.class
      );

      TransactionRequest transactionRequest = new TransactionRequest();
      transactionRequest.setAccountId(request.getAccountId());
      transactionRequest.setAmount(request.getAmount());
      transactionService.withdraw(transactionRequest);

      return response;
    } catch (RestClientException e) {
      throw new InvestmentOperationException("Failed to process investment", e);
    }
  }

  @Override
  public List<InvestmentResponse> getAllUsersInvestments() {
    Long currentUserId = accountService.getCurrentSessionUser().getId();

    try {
      ResponseEntity<List<InvestmentResponse>> response = restTemplate.exchange(
              "http://localhost:8092/investments?userId=" + currentUserId,
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
  public BalanceCheckResponse checkBalance(BalanceCheckRequest request) {
    Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();
    BalanceCheckResponse balanceCheckResponse = new BalanceCheckResponse();
    balanceCheckResponse.setCurrentBalance(account.getBalance());

    balanceCheckResponse.setSufficientFunds(account.getBalance() >= request.getAmount());

    return balanceCheckResponse;
  }

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
