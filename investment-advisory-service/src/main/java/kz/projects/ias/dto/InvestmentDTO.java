package kz.projects.ias.dto;

import kz.projects.ias.models.enums.InvestmentType;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InvestmentDTO {
  private Long id;
  private Long userId;
  private Long accountId;
  private InvestmentType investmentType;
  private Double amount;
  private Date date;
}
