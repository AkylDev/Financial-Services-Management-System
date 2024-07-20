package kz.projects.ias.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(FinancialAdvisorAlreadyExistsException.class)
  public ResponseEntity<String> handleFinancialAdvisorAlreadyExistsException(FinancialAdvisorAlreadyExistsException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AdvisorySessionNotFoundException.class)
  public ResponseEntity<String> handleAdvisorySessionNotFoundException(AdvisorySessionNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(CheckBalanceException.class)
  public ResponseEntity<String> handleCheckBalanceException(CheckBalanceException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(CustomerServiceRequestNotFoundException.class)
  public ResponseEntity<String> handleCustomerServiceRequestNotFoundException(CustomerServiceRequestNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(FinancialAdvisorNotFoundException.class)
  public ResponseEntity<String> handleFinancialAdvisorNotFoundException(FinancialAdvisorNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(InvestmentNotFoundException.class)
  public ResponseEntity<String> handleInvestmentNotFoundException(InvestmentNotFoundException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(NotSufficientFundsException.class)
  public ResponseEntity<String> handleNotSufficientFundsException(NotSufficientFundsException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

}
