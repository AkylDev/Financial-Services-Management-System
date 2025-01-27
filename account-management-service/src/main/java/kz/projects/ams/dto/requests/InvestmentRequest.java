package kz.projects.ams.dto.requests;

import kz.projects.commonlib.dto.enums.InvestmentType;

public record InvestmentRequest(
        Long id,
        Long userId,
        Long accountId,
        InvestmentType investmentType,
        Double amount
) {}
