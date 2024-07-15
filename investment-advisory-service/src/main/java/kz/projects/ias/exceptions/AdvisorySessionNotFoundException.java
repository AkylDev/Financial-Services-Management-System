package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdvisorySessionNotFoundException extends RuntimeException {
  public AdvisorySessionNotFoundException(String message){
    super(message);
  }
}
