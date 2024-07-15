package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CheckBalanceException extends RuntimeException {
  public CheckBalanceException(String message) {
    super(message);
  }

  public CheckBalanceException(String message, Throwable cause) {
    super(message, cause);
  }
}
