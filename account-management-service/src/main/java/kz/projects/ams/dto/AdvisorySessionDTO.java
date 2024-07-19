package kz.projects.ams.dto;


import java.time.LocalDate;
import java.time.LocalTime;

public record AdvisorySessionDTO(
        Long id,
        Long userId,
        Long advisoryId,
        LocalDate date,
        LocalTime time
) {}

