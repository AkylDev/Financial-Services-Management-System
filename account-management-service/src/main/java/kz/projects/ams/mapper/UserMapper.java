package kz.projects.ams.mapper;

import kz.projects.ams.dto.UserDTO;
import kz.projects.ams.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public UserDTO toDto(User user) {
    if (user == null) {
      return null;
    }

    return new UserDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            null // Assuming password is not included in DTO for security reasons
    );
  }
}

