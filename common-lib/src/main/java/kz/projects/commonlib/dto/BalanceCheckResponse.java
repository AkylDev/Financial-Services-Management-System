package kz.projects.commonlib.dto;

public record BalanceCheckResponse(boolean sufficientFunds, Double currentBalance) {}

