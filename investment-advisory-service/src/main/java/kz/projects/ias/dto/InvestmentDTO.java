package kz.projects.ias.dto;

import kz.projects.commonlib.dto.enums.InvestmentType;

import java.util.Date;

public record InvestmentDTO(
        Long id,
        Long userId,
        Long accountId,
        InvestmentType investmentType,
        Double amount,
        Date date
) {}