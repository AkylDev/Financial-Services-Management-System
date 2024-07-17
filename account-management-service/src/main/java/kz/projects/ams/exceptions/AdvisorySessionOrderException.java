package kz.projects.ams.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class AdvisorySessionOrderException extends RuntimeException {
  public AdvisorySessionOrderException(String message, Throwable cause) {
    super(message, cause);
  }
}
