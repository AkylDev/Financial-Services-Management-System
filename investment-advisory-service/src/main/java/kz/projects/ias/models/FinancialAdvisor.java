package kz.projects.ias.models;

import jakarta.persistence.*;
import kz.projects.ias.models.enums.AdvisorSpecialization;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "t_advisors")
public class FinancialAdvisor {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String email;

  private String name;

  @Enumerated(EnumType.STRING)
  private AdvisorSpecialization specialization;


}
