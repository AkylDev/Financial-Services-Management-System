package kz.projects.ams.dto.requests;


public record TransactionRequest(
        Long accountId,
        Double amount
) {}

