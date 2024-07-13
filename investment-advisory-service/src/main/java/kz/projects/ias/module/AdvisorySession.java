package kz.projects.ias.module;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "t_advisories")
public class AdvisorySession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "user_id")
  private Long user_id;

  @ManyToOne
  @JoinColumn(name = "advisor_id")
  private FinancialAdvisor financialAdvisor;

  private LocalDate date;

  private LocalTime time;

  private String status;

}
