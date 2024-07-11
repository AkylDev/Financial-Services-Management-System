package kz.projects.ams.services;

import kz.projects.ams.model.User;

import java.util.Optional;

public interface UserService {

  User register(User user);

  Optional<User> findUserByEmail(String email);

}
