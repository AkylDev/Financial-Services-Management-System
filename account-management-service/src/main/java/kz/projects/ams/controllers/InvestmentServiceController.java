package kz.projects.ams.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ams.dto.requests.InvestmentRequest;
import kz.projects.ams.dto.responses.InvestmentResponse;
import kz.projects.ams.services.UserInvestmentService;
import kz.projects.commonlib.dto.BalanceCheckRequest;
import kz.projects.commonlib.dto.BalanceCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ams")
public class InvestmentServiceController {

  private final UserInvestmentService investmentAdvisoryService;

  @Operation(summary = "Create a new investment")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Investment created successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/to-invest")
  public ResponseEntity<InvestmentResponse> toInvest(@RequestBody InvestmentRequest request) {
    return new ResponseEntity<>(investmentAdvisoryService.toInvest(request), HttpStatus.CREATED);
  }

  @Operation(summary = "Get all user investments")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of investments retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvestmentResponse.class))),
          @ApiResponse(responseCode = "404", description = "Investments not found", content = @Content)
  })
  @GetMapping("/view-investments")
  public ResponseEntity<List<InvestmentResponse>> getAllUsersInvestments() {
    List<InvestmentResponse> investmentResponses = investmentAdvisoryService.getAllUsersInvestments();
    return new ResponseEntity<>(investmentResponses, HttpStatus.OK);
  }

  @Operation(summary = "Delete an investment")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Investment deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Investment not found", content = @Content)
  })
  @DeleteMapping("/delete-investment/{id}")
  public ResponseEntity<Void> deleteInvestments(@PathVariable("id") Long id) {
    investmentAdvisoryService.deleteInvestment(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @Operation(summary = "Check balance")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Balance checked successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = BalanceCheckResponse.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/check-balance")
  public ResponseEntity<BalanceCheckResponse> checkBalance(@RequestBody BalanceCheckRequest request) {
    return new ResponseEntity<>(investmentAdvisoryService.checkBalance(request), HttpStatus.OK);
  }
}
