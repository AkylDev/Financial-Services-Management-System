package kz.projects.ias.repositories;

import kz.projects.ias.models.AdvisorySession;
import kz.projects.ias.models.FinancialAdvisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdvisorySessionRepository extends JpaRepository<AdvisorySession, Long> {
  List<AdvisorySession> findAllByUserId(Long userId);

  List<AdvisorySession> findAllByFinancialAdvisor(FinancialAdvisor advisor);
}
