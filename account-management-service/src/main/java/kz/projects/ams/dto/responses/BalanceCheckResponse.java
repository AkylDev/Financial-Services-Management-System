package kz.projects.ams.dto.responses;

public record BalanceCheckResponse(
        boolean sufficientFunds,
        Double currentBalance
) {}

