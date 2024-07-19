package kz.projects.ams.services.impl;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.exceptions.UnauthorizedException;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.AccountMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

  private final AccountRepository accountRepository;

  private final AccountMapper accountMapper;

  public User getCurrentSessionUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      return (User) authentication.getPrincipal();
    }
    return null;
  }

  @Override
  public AccountDTO createAccount(AccountDTO accountRequest) {
    Account account = new Account();
    account.setUser(getCurrentSessionUser());
    account.setAccountType(accountRequest.accountType());
    account.setBalance(accountRequest.balance());

    return accountMapper.toDto(accountRepository.save(account));
  }

  @Override
  public List<AccountDTO> findAccountsByUserId() {
    User currentUser = getCurrentSessionUser();
    List<Account> accounts = accountRepository.findAllByUser(currentUser);
    return accounts.stream()
            .map(accountMapper::toDto)
            .collect(Collectors.toList());
  }


  @Override
  public AccountDTO updateAccount(Long id, AccountDTO request) {
    Optional<Account> accountOptional = accountRepository.findById(id);

    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();

    if (!account.getUser().getId().equals(getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not authorized to change this appointment");
    }

    account.setAccountType(request.accountType());
    account.setBalance(request.balance());

    return accountMapper.toDto(accountRepository.save(account));
  }

  @Override
  public void deleteAccount(Long id) {
    Optional<Account> accountOptional = accountRepository.findById(id);

    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account account = accountOptional.get();

    if (!account.getUser().getId().equals(getCurrentSessionUser().getId())){
      throw new UnauthorizedException("You are not authorized to change this appointment");
    }

    accountRepository.deleteById(id);
  }
}
