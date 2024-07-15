package kz.projects.ias.module;

import jakarta.persistence.*;
import kz.projects.ias.module.enums.InvestmentType;
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

  private Long userId;

  @Enumerated(EnumType.STRING)
  private InvestmentType investmentType;

  private Double amount;

  @Temporal(TemporalType.TIMESTAMP)
  private Date date;
}
