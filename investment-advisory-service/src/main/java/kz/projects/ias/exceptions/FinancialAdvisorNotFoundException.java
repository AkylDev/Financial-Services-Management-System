package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FinancialAdvisorNotFoundException extends RuntimeException {
  public FinancialAdvisorNotFoundException(String message){
    super(message);
  }
}
