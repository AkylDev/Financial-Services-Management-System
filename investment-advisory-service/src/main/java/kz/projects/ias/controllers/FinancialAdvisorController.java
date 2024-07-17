package kz.projects.ias.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "Add a new financial advisor")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Financial advisor added successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = FinancialAdvisor.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<FinancialAdvisor> addFinancialAdvisor(@RequestBody FinancialAdvisorDTO advisor) {
    return new ResponseEntity<>(financialAdvisorService.addFinancialAdvisor(advisor), HttpStatus.CREATED);
  }

  @Operation(summary = "Get all financial advisors")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Financial advisors retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = FinancialAdvisor.class))),
          @ApiResponse(responseCode = "404", description = "Financial advisors not found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<FinancialAdvisor>> getAllAdvisor() {
    return new ResponseEntity<>(financialAdvisorService.getAllAdvisors(), HttpStatus.OK);
  }

  @Operation(summary = "Delete a financial advisor by ID")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Financial advisor deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Financial advisor not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteFinancialAdvisor(@PathVariable("id") Long id) {
    financialAdvisorService.deleteFinancialAdvisor(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
