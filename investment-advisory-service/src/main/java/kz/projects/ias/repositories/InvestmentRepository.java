package kz.projects.ias.repositories;

import kz.projects.ias.models.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
  List<Investment> findAllByUserId(Long userId);
}
