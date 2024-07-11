package kz.projects.ams.repositories;

import kz.projects.ams.model.Account;
import kz.projects.ams.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  List<Account> findAllByUser(User user);

  @NonNull
  Optional<Account> findById(@NonNull Long id);
}
