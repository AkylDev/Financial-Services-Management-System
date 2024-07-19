package kz.projects.ams.dto.requests;


public record TransferRequest(
        Long fromAccount,
        Long toAccount,
        Double amount
) {}
