package kz.projects.ams.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {
  private Long fromAccount;
  private Long toAccount;
  private Double amount;
}
