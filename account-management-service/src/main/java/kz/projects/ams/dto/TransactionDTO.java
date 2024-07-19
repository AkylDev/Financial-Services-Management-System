package kz.projects.ams.dto;

import kz.projects.ams.models.enums.TransactionType;


import java.util.Date;

public record TransactionDTO(
        Long id,
        Long accountId,
        TransactionType type,
        Double amount,
        Date date
) {}

