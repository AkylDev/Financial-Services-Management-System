package kz.projects.ams.dto;

import kz.projects.ams.models.enums.AccountType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDTO {
  private Long id;
  private String email;
  private AccountType accountType;
  private Double balance;
}
