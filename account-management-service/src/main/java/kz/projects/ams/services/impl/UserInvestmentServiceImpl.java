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
import kz.projects.ams.services.UserInvestmentService;
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
public class UserInvestmentServiceImpl implements UserInvestmentService {

  private final RestTemplate restTemplate;

  private final AccountService accountService;

  private final TransactionService transactionService;

  private final AccountRepository accountRepository;

  @Override
  public InvestmentResponse toInvest(InvestmentRequest request) {
    Optional<Account> accountOptional = accountRepository.findById(request.accountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found!");
    }

    Long currentUserId = accountService.getCurrentSessionUser().getId();
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

      TransactionRequest transactionRequest = new TransactionRequest(
              request.accountId(),
              request.amount()
      );
      transactionService.withdraw(transactionRequest);

      return response;
    } catch (RestClientException e) {
      throw new InvestmentOperationException("Failed to process investment", e);
    }
  }


  @Override
  public void updateInvestment(Long id, InvestmentRequest request) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
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
              AdvisorySessionDTO.class
      );
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to update the investment", e);
    }
  }

  @Override
  public void deleteInvestment(Long id) {
    Long currentUserId = accountService.getCurrentSessionUser().getId();
    try {
      restTemplate.delete(
              "http://localhost:8092/investments/{id}?userId={userId}",
              id, currentUserId
      );
    } catch (RestClientException e) {
      throw new AdvisorySessionOrderException("Failed to delete the investment", e);
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
