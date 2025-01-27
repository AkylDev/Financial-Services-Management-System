package kz.projects.ias.models;

import jakarta.persistence.*;
import kz.projects.commonlib.dto.enums.AdvisorSpecialization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
