package kz.projects.ams.repositories;


import kz.projects.ams.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  @Query("SELECT t FROM Transaction t WHERE t.account.user.id = :userId")
  List<Transaction> findAllByUserId(@Param("userId") Long userId);
}
