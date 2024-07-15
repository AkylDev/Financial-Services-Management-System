package kz.projects.ams.mapper;

import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserDTO toDto(User user){
    if (user == null){
      return null;
    }

    UserDTO userDTO = new UserDTO();
    userDTO.setId(user.getId());
    userDTO.setName(user.getName());
    userDTO.setEmail(user.getEmail());

    return userDTO;
  }
}
