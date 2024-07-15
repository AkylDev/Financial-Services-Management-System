package kz.projects.ams.dto;

import kz.projects.ams.models.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TransactionDTO {
  private Long id;
  private Long accountId;
  private TransactionType type;
  private Double amount;
  private Date date;
}
