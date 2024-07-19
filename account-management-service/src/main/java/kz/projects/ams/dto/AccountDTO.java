package kz.projects.ams.dto;

import kz.projects.ams.models.enums.AccountType;

public record AccountDTO(
        Long id,
        String email,
        AccountType accountType,
        Double balance
) {}
