package kz.projects.ias.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceCheckResponse {
  private boolean sufficientFunds;
  private Double currentBalance;
}
