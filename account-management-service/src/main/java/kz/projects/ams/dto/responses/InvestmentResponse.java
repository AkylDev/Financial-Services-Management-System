package kz.projects.ams.dto.responses;


import java.util.Date;

public record InvestmentResponse(
        Long id,
        Long userId,
        InvestmentType investmentType,
        Double amount,
        Date date
) {}
