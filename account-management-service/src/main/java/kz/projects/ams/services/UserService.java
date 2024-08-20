package kz.projects.ams.services;

import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.models.User;
import org.springframework.security.core.userdetails.UserDetails;


public interface UserService {

  UserDTO register(UserDTO user);
  UserDetails login(LoginRequest request);
  UserDTO registerAsAdvisor(AdviserDTO adviser);
  User getCurrentSessionUser();
}
