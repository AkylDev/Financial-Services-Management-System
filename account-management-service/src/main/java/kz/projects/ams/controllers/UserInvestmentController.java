package kz.projects.ams.controllers;

import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.services.UserInvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserInvestmentController {

  private final UserInvestmentService userInvestmentService;

  @PostMapping("/to-invest")
  public ResponseEntity<InvestmentResponse> toInvest(@RequestBody InvestmentRequest request){
    return new ResponseEntity<>(userInvestmentService.toInvest(request), HttpStatus.OK);
  }

  @PostMapping("/check-balance")
  public ResponseEntity<BalanceCheckResponse> checkBalance(@RequestBody BalanceCheckRequest request) {
    return new ResponseEntity<>(userInvestmentService.checkBalance(request), HttpStatus.OK);
  }
}
