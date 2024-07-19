package kz.projects.ias.dto;

import kz.projects.ias.models.enums.AdvisorSpecialization;

public record FinancialAdvisorDTO(
        Long id,
        String name,
        String email,
        String password,
        AdvisorSpecialization specialization
) {}
