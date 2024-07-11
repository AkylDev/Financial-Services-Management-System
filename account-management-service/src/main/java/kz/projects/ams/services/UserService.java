package kz.projects.ams.services;

import kz.projects.ams.dto.LoginRequest;
import kz.projects.ams.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserService {

  User register(User user);
  UserDetails login(LoginRequest request);

  Optional<User> findUserByEmail(String email);

}
