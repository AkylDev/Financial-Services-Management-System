package kz.projects.ias.repositories;

import kz.projects.ias.models.CustomerServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerServiceRequestRepository extends JpaRepository<CustomerServiceRequest, Long> {
}
