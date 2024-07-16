package kz.projects.ias.dto;

import kz.projects.ias.models.enums.AdvisorSpecialization;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FinancialAdvisorDTO {
  private Long id;
  private String name;
  private String email;
  private String password;
  private AdvisorSpecialization specialization;
}
