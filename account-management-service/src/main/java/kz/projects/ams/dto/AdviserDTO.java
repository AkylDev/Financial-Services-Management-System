package kz.projects.ams.dto;

import kz.projects.commonlib.dto.enums.AdvisorSpecialization;

public record AdviserDTO(
        String name,
        String email,
        String password,
        AdvisorSpecialization specialization
) {}
