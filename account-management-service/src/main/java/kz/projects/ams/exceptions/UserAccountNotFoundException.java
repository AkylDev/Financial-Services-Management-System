package kz.projects.ams.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserAccountNotFoundException extends RuntimeException {
  public UserAccountNotFoundException(String message) {
    super(message);
  }
}
