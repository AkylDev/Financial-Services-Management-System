package kz.projects.ias.dto;

import kz.projects.ias.module.InvestmentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestmentDTO {
  private Long userId;
  private Long accountId;
  private InvestmentType investmentType;
  private Double amount;
}
