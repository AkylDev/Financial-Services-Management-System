package kz.projects.ias.controllers;

import kz.projects.ias.dto.InvestmentDTO;
import kz.projects.ias.models.Investment;
import kz.projects.ias.service.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/investments")
public class InvestmentController {

  private final InvestmentService investmentService;

  @PostMapping
  public ResponseEntity<InvestmentDTO> createInvestment(@RequestBody InvestmentDTO investmentDTO) {
    InvestmentDTO savedInvestment = investmentService.createInvestment(investmentDTO);
    return new ResponseEntity<>(savedInvestment, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Investment>> getAllInvestments(@RequestParam("userId") Long userId) {
    List<Investment> investmentList = investmentService.getAllInvestments(userId);
    return new ResponseEntity<>(investmentList, HttpStatus.OK);
  }

  @PutMapping
  public ResponseEntity<Void> updateInvestment(@RequestBody InvestmentDTO investment) {
    investmentService.updateInvestment(investment);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInvestment(@PathVariable("id") Long id,
                               @RequestParam("userId") Long userId) {
    investmentService.deleteInvestment(id, userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }


}
