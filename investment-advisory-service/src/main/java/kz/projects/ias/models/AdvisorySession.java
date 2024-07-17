package kz.projects.ias.models;

import jakarta.persistence.*;
import kz.projects.ias.models.enums.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_advisories")
public class AdvisorySession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long userId;

  @ManyToOne
  @JoinColumn(name = "advisor_id")
  private FinancialAdvisor financialAdvisor;

  private LocalDate date;

  private LocalTime time;

  @Enumerated(EnumType.STRING)
  private RequestStatus status;

}
