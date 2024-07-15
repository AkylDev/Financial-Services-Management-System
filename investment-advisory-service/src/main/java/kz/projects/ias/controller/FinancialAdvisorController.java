package kz.projects.ias.controller;

import kz.projects.ias.module.FinancialAdvisor;
import kz.projects.ias.service.FinancialAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/financial-advisor")
public class FinancialAdvisorController {

  private final FinancialAdvisorService financialAdvisorService;

  @PostMapping
  public ResponseEntity<FinancialAdvisor> addFinancialAdvisor(@RequestBody FinancialAdvisor advisor) {
    return new ResponseEntity<>(financialAdvisorService.addFinancialAdvisor(advisor), HttpStatus.CREATED);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFinancialAdvisor(@PathVariable("id") Long id) {
    financialAdvisorService.deleteFinancialAdvisor(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
