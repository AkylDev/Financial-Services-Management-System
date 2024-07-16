package kz.projects.ams.dto.requests;

import kz.projects.ams.dto.responses.InvestmentType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvestmentRequest {
  private Long id;
  private Long userId;
  private Long accountId;
  private InvestmentType investmentType;
  private Double amount;
}
