package kz.projects.ams.controllers;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.services.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class AccountController {

  private final AccountService accountService;

  @PostMapping
  public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountDTO account) {
    return new ResponseEntity<>(accountService.createAccount(account), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<AccountDTO>> getUserAccounts() {
    return new ResponseEntity<>(accountService.findAccountsByUserId(), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<AccountDTO> updateAccount(@PathVariable(name = "id") Long id,
                                               @RequestBody AccountDTO request) {
    return new ResponseEntity<>(accountService.updateAccount(id, request), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable(name = "id") Long id) {
    accountService.deleteAccount(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
