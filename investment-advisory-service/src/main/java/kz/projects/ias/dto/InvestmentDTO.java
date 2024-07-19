package kz.projects.ias.dto;

import kz.projects.ias.models.enums.InvestmentType;

import java.util.Date;

public record InvestmentDTO(
        Long id,
        Long userId,
        Long accountId,
        InvestmentType investmentType,
        Double amount,
        Date date
) {}