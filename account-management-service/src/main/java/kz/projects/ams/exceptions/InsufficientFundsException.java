package kz.projects.ams.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)

public class InsufficientFundsException extends RuntimeException {
  public InsufficientFundsException(String message){
    super(message);
  }
}
