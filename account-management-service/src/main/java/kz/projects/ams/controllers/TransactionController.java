package kz.projects.ams.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.requests.TransferRequest;
import kz.projects.ams.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  @Operation(summary = "Deposit an amount")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Amount deposited successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/deposit")
  public ResponseEntity<TransactionDTO> deposit(@RequestBody TransactionRequest request) {
    return new ResponseEntity<>(transactionService.deposit(request), HttpStatus.OK);
  }

  @Operation(summary = "Withdraw an amount")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Amount withdrawn successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/withdraw")
  private ResponseEntity<TransactionDTO> withdrawal(@RequestBody TransactionRequest request) {
    return new ResponseEntity<>(transactionService.withdraw(request), HttpStatus.OK);
  }

  @Operation(summary = "Transfer an amount")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Amount transferred successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping("/transfer")
  private ResponseEntity<TransactionDTO> transferAmount(@RequestBody TransferRequest request) {
    return new ResponseEntity<>(transactionService.transfer(request), HttpStatus.OK);
  }

  @Operation(summary = "Get all transactions")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
          @ApiResponse(responseCode = "404", description = "Transactions not found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<TransactionDTO>> getTransactions() {
    return new ResponseEntity<>(transactionService.getTransactions(), HttpStatus.OK);
  }
}
