package kz.projects.ams.services;

import kz.projects.ams.dto.AdviserDTO;
import kz.projects.ams.dto.requests.LoginRequest;
import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.models.User;


public interface UserService {

  UserDTO register(UserDTO user);
  UserDTO login(LoginRequest request);
  UserDTO registerAsAdvisor(AdviserDTO adviser);
  User getCurrentSessionUser();
}
