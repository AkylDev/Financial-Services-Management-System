package kz.projects.ias.repositories;

import kz.projects.ias.models.AdvisorySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvisorySessionRepository extends JpaRepository<AdvisorySession, Long> {
}
