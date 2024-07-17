package kz.projects.ams.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdvisorySessionDTO {
  private Long id;

  private Long userId;

  private Long advisoryId;

  private LocalDate date;

  private LocalTime time;
}
