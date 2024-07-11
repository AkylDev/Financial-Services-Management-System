package kz.projects.ams.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionRequest {
  private Long accountId;
  private Double amount;
}
