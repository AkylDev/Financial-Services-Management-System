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

    InvestmentDTO dto = new InvestmentDTO();
    dto.setId(investment.getId());
    dto.setUserId(investment.getUserId());
    dto.setDate(investment.getDate());
    dto.setAmount(investment.getAmount());
    dto.setInvestmentType(investment.getInvestmentType());

    return dto;
  }

  public static Investment toEntity(InvestmentDTO dto) {
    Investment investment = new Investment();
    investment.setUserId(dto.getUserId());
    investment.setAmount(dto.getAmount());
    investment.setInvestmentType(dto.getInvestmentType());
    return investment;
  }

}
