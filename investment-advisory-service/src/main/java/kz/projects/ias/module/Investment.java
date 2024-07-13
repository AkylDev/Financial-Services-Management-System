package kz.projects.ias.module;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "t_investments")
public class Investment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JoinColumn(name = "user_id")
  private Long user_id;

  @Enumerated(EnumType.STRING)
  private InvestmentType investmentType;

  private Double amount;

  @Temporal(TemporalType.TIMESTAMP)
  private Date date;
}
