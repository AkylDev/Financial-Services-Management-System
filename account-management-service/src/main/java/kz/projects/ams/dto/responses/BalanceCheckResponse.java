package kz.projects.ams.dto.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceCheckResponse {
  private boolean sufficientFunds;
  private Double currentBalance;
}
