package kz.projects.ias.controllers;

import kz.projects.ias.dto.FinancialAdvisorDTO;
import kz.projects.ias.models.FinancialAdvisor;
import kz.projects.ias.service.FinancialAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/financial-advisors")
public class FinancialAdvisorController {

  private final FinancialAdvisorService financialAdvisorService;

  @PostMapping
  public ResponseEntity<FinancialAdvisor> addFinancialAdvisor(@RequestBody FinancialAdvisorDTO advisor) {
    return new ResponseEntity<>(financialAdvisorService.addFinancialAdvisor(advisor), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<FinancialAdvisor>> getAllAdvisor() {
    return new ResponseEntity<>(financialAdvisorService.getAllAdvisors(), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFinancialAdvisor(@PathVariable("id") Long id) {
    financialAdvisorService.deleteFinancialAdvisor(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
