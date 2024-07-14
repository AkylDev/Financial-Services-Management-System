package kz.projects.ams.services.impl;

import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.model.Account;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.UserInvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInvestmentServiceImpl implements UserInvestmentService {

  private final RestTemplate restTemplate;

  private final AccountService accountService;

  private final AccountRepository accountRepository;

  @Override
  public InvestmentResponse toInvest(InvestmentRequest request){
    Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());
    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found!");
    }

    Long currentUserId = accountService.getCurrentSessionUser().getId();
    request.setUserId(currentUserId);

    return restTemplate.postForObject(
            "http://localhost:8092/investments",
            request,
            InvestmentResponse.class
    );
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

}
