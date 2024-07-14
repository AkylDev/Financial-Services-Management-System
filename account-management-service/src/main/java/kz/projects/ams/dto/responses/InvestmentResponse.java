package kz.projects.ams.dto.responses;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class InvestmentResponse {
  private Long id;
  private Long userId;
  private InvestmentType investmentType;
  private Double amount;
  private Date date;
}
