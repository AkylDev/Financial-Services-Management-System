package kz.projects.ias.repositories;

import kz.projects.ias.models.FinancialAdvisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialAdvisorRepository extends JpaRepository<FinancialAdvisor, Long> {
}
