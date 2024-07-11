package kz.projects.ams.mapper;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.model.Account;
import kz.projects.ams.model.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public AccountDTO toDto(Account account) {
    if (account == null) {
      return null;
    }

    AccountDTO accountDTO = new AccountDTO();
    accountDTO.setId(account.getId());
    accountDTO.setEmail(account.getUser().getEmail());
    accountDTO.setAccountType(account.getAccountType());
    accountDTO.setBalance(account.getBalance());

    return accountDTO;
  }

  public Account toModel(AccountDTO accountDTO, User user) {
    if (accountDTO == null) {
      return null;
    }

    Account account = new Account();
    account.setUser(user);
    account.setAccountType(accountDTO.getAccountType());
    account.setBalance(accountDTO.getBalance());

    return account;
  }
}
