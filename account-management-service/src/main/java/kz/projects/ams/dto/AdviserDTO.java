package kz.projects.ams.dto;

import kz.projects.ams.models.enums.AdvisorSpecialization;

public record AdviserDTO(
        String name,
        String email,
        String password,
        AdvisorSpecialization specialization
) {}
