package kz.projects.ias.controller;

import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.module.Investment;
import kz.projects.ias.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/investments")
public class InvestmentController {

  private final InvestmentService investmentService;

  @PostMapping
  public ResponseEntity<Investment> createInvestment(@RequestBody InvestmentDTO investmentDTO) {
    Investment savedInvestment = investmentService.createInvestment(investmentDTO);
    return new ResponseEntity<>(savedInvestment, HttpStatus.CREATED);
  }

}
