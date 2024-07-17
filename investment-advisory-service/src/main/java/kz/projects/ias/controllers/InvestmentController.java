package kz.projects.ias.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

  @Operation(summary = "Create a new investment")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Investment created successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<InvestmentDTO> createInvestment(@RequestBody InvestmentDTO investmentDTO) {
    InvestmentDTO savedInvestment = investmentService.createInvestment(investmentDTO);
    return new ResponseEntity<>(savedInvestment, HttpStatus.CREATED);
  }

  @Operation(summary = "Get all investments for a user")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Investments retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = Investment.class))),
          @ApiResponse(responseCode = "404", description = "Investments not found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<Investment>> getAllInvestments(@RequestParam("userId") Long userId) {
    List<Investment> investmentList = investmentService.getAllInvestments(userId);
    return new ResponseEntity<>(investmentList, HttpStatus.OK);
  }

  @Operation(summary = "Update an existing investment")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Investment updated successfully", content = @Content),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content),
          @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
  })
  @PutMapping
  public ResponseEntity<Void> updateInvestment(@RequestBody InvestmentDTO investment) {
    investmentService.updateInvestment(investment);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Delete an investment")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Investment deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
  })

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteInvestment(@PathVariable("id") Long id,
                                               @RequestParam("userId") Long userId) {
    investmentService.deleteInvestment(id, userId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
