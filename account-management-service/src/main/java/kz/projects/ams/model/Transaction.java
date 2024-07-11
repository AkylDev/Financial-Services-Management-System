package kz.projects.ams.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "t_transactions")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  @Enumerated(EnumType.STRING)
  private TransactionType type;

  private Double amount;

  @Temporal(TemporalType.TIMESTAMP)
  private Date date;
}