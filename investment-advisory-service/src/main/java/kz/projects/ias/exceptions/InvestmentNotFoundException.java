package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class InvestmentNotFoundException extends RuntimeException {
  public InvestmentNotFoundException(String message){
    super(message);
  }
}
