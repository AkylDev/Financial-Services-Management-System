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

  @PutMapping("/{id}")
  public ResponseEntity<Investment> updateInvestment(@PathVariable("id") Long id,
                                                     @RequestBody InvestmentDTO investment) {
    Investment updatedInvestment = investmentService.updateInvestment(id, investment);
    return new ResponseEntity<>(updatedInvestment, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public void deleteInvestment(@PathVariable("id") Long id) {
    investmentService.deleteInvestment(id);
  }


}
