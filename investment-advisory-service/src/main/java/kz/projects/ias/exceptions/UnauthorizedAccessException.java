package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class UnauthorizedAccessException extends RuntimeException {
  public UnauthorizedAccessException(String message) {
    super(message);
  }
}

