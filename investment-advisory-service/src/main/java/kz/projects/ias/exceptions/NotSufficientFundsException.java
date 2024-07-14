package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotSufficientFundsException extends RuntimeException {
  public NotSufficientFundsException(String message){
    super(message);
  }
}
