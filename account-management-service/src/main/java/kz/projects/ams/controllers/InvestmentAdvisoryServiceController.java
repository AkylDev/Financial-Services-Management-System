package kz.projects.ams.controllers;

import kz.projects.ams.dto.AdvisorySessionDTO;
import kz.projects.ams.dto.requests.BalanceCheckRequest;
import kz.projects.ams.dto.responses.BalanceCheckResponse;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.services.UserInvestmentAdvisoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvestmentAdvisoryServiceController {

  private final UserInvestmentAdvisoryService investmentAdvisoryService;

  @PostMapping("/to-invest")
  public ResponseEntity<InvestmentResponse> toInvest(@RequestBody InvestmentRequest request) {
    return new ResponseEntity<>(investmentAdvisoryService.toInvest(request), HttpStatus.CREATED);
  }

  @PostMapping("/check-balance")
  public ResponseEntity<BalanceCheckResponse> checkBalance(@RequestBody BalanceCheckRequest request) {
    return new ResponseEntity<>(investmentAdvisoryService.checkBalance(request), HttpStatus.OK);
  }

  @PostMapping("/book-advisory")
  public ResponseEntity<AdvisorySessionDTO> orderAdvisorySession(@RequestBody AdvisorySessionDTO request) {
    return new ResponseEntity<>(investmentAdvisoryService.orderAdvisorySession(request), HttpStatus.CREATED);
  }

  @GetMapping("/view-advisories")
  public ResponseEntity<List<AdvisorySessionDTO>> getAdvisorySessionsPlanned() {
    List<AdvisorySessionDTO> advisorySessions = investmentAdvisoryService.getAdvisorySessionsPlanned();
    return new ResponseEntity<>(advisorySessions, HttpStatus.OK);
  }

  @PutMapping("/reschedule-advisory/{id}")
  public ResponseEntity<Void> rescheduleAdvisorySession(@PathVariable("id") Long id,
                                        @RequestBody AdvisorySessionDTO request) {
    investmentAdvisoryService.rescheduleAdvisorySession(id, request);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/delete-advisory/{id}")
  public ResponseEntity<Void> deleteAdvisorySession(@PathVariable("id") Long id) {
    investmentAdvisoryService.deleteAdvisorySession(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
