package kz.projects.ias.mapper;

import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.models.Investment;
import org.springframework.stereotype.Component;

@Component
public class InvestmentsMapper {

  public static InvestmentDTO toDto(Investment investment) {
    if (investment == null) {
      return null;
    }

    return new InvestmentDTO(
            investment.getId(),
            investment.getUserId(),
            null,
            investment.getInvestmentType(),
            investment.getAmount(),
            investment.getDate()
    );
  }

  public static Investment toEntity(InvestmentDTO dto) {
    Investment investment = new Investment();
    investment.setUserId(dto.userId());
    investment.setAmount(dto.amount());
    investment.setInvestmentType(dto.investmentType());
    return investment;
  }

}
