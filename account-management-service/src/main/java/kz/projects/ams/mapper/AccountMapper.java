package kz.projects.ams.mapper;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.models.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

  public AccountDTO toDto(Account account) {
    if (account == null) {
      return null;
    }

    return new AccountDTO(
            account.getId(),
            account.getUser().getEmail(),
            account.getAccountType(),
            account.getBalance()
    );
  }
}
