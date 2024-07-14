package kz.projects.ias.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BalanceCheckRequest {
  private Long accountId;
  private Double amount;
}
