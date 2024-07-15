package kz.projects.ias.module;

import jakarta.persistence.*;
import kz.projects.ias.module.enums.RequestStatus;
import kz.projects.ias.module.enums.RequestType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "t_customer_requests")
public class CustomerServiceRequest {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private RequestType requestType;

  private String description;

  @Enumerated(EnumType.STRING)
  private RequestStatus status;
}
