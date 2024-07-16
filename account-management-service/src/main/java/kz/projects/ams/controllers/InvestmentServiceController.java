package kz.projects.ams.controllers;

import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.services.UserInvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvestmentServiceController {

  private final UserInvestmentService investmentAdvisoryService;

  @PostMapping("/to-invest")
  public ResponseEntity<InvestmentResponse> toInvest(@RequestBody InvestmentRequest request) {
    return new ResponseEntity<>(investmentAdvisoryService.toInvest(request), HttpStatus.CREATED);
  }

  @GetMapping("/view-investments")
  public ResponseEntity<List<InvestmentResponse>> getAllUsersInvestments() {
    List<InvestmentResponse> investmentResponses = investmentAdvisoryService.getAllUsersInvestments();
    return new ResponseEntity<>(investmentResponses, HttpStatus.CREATED);
  }

  @PutMapping("/update-investment/{id}")
  public ResponseEntity<Void> updateInvest(@PathVariable("id") Long id,
                                           @RequestBody InvestmentRequest request) {
    investmentAdvisoryService.updateInvestment(id, request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/delete-investment/{id}")
  public ResponseEntity<Void> deleteInvestments(@PathVariable("id") Long id) {
    investmentAdvisoryService.deleteInvestment(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @PostMapping("/check-balance")
  public ResponseEntity<BalanceCheckResponse> checkBalance(@RequestBody BalanceCheckRequest request) {
    return new ResponseEntity<>(investmentAdvisoryService.checkBalance(request), HttpStatus.OK);
  }
}
