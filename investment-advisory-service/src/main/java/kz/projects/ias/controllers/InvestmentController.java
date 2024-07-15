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
  public ResponseEntity<Investment> createInvestment(@RequestBody InvestmentDTO investmentDTO) {
    Investment savedInvestment = investmentService.createInvestment(investmentDTO);
    return new ResponseEntity<>(savedInvestment, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Investment>> getAllInvestments(){
    return new ResponseEntity<>(investmentService.getAllInvestments(), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Investment> updateInvestment(@PathVariable("id") Long id,
                                                     @RequestBody Investment investment){
    return new ResponseEntity<>(investmentService.updateInvestment(id, investment), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public void deleteInvestment(@PathVariable("id") Long id){
    investmentService.deleteInvestment(id);
  }


}
