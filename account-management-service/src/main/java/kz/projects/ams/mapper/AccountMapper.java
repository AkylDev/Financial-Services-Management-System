package kz.projects.ams.mapper;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.User;
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
}
