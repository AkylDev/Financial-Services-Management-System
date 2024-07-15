package kz.projects.ams.repositories;

import kz.projects.ams.models.Permissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionsRepository extends JpaRepository<Permissions, Long> {
  Permissions findByRole(String role);
}
