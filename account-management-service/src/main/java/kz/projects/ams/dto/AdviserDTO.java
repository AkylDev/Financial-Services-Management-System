package kz.projects.ams.dto;

import kz.projects.ams.models.enums.AdvisorSpecialization;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdviserDTO {
  private String name;
  private String email;
  private String password;
  private AdvisorSpecialization specialization;
}
