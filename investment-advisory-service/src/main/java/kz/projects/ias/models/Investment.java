package kz.projects.ias.models;

import jakarta.persistence.*;
import kz.projects.ias.models.enums.InvestmentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
