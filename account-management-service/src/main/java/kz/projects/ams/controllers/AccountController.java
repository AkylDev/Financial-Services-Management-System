package kz.projects.ams.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ams/accounts")
public class AccountController {

  private final AccountService accountService;

  @Operation(summary = "Create a new account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "201", description = "Account created successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
          @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
  })
  @PostMapping
  public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO account) {
    return new ResponseEntity<>(accountService.createAccount(account), HttpStatus.CREATED);
  }

  @Operation(summary = "Get all user accounts")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Found user accounts",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
          @ApiResponse(responseCode = "404", description = "No accounts found", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<AccountDTO>> getUserAccounts() {
    return new ResponseEntity<>(accountService.findAccountsByUserId(), HttpStatus.OK);
  }

  @Operation(summary = "Update an existing account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Account updated successfully",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
          @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
  })
  @PutMapping("/{id}")
  public ResponseEntity<AccountDTO> updateAccount(@PathVariable(name = "id") Long id,
                                                  @RequestBody AccountDTO request) {
    return new ResponseEntity<>(accountService.updateAccount(id, request), HttpStatus.OK);
  }

  @Operation(summary = "Delete an account")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "204", description = "Account deleted successfully", content = @Content),
          @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable(name = "id") Long id) {
    accountService.deleteAccount(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
