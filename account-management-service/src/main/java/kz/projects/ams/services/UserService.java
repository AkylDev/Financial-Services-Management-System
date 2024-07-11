package kz.projects.ams.services;

import kz.projects.ams.dto.LoginRequest;
import kz.projects.ams.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserService {

  UserDTO register(UserDTO user);
  UserDetails login(LoginRequest request);
}
