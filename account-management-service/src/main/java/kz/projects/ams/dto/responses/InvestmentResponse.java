package kz.projects.ams.dto.responses;

import kz.projects.commonlib.dto.enums.InvestmentType;

import java.util.Date;

public record InvestmentResponse(
        Long id,
        Long userId,
        InvestmentType investmentType,
        Double amount,
        Date date
) {
}
