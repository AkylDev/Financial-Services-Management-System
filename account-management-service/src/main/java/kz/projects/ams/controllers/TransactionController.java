package kz.projects.ams.controllers;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.TransactionRequest;
import kz.projects.ams.dto.TransferRequest;
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

  @PostMapping("/deposit")
  public ResponseEntity<TransactionDTO> deposit(@RequestBody TransactionRequest request) {
    return new ResponseEntity<>(transactionService.deposit(request), HttpStatus.OK);
  }

  @PostMapping("/withdraw")
  private ResponseEntity<TransactionDTO> withdrawal(@RequestBody TransactionRequest request) {
    return new ResponseEntity<>(transactionService.withdraw(request), HttpStatus.OK);
  }

  @PostMapping("/transfer")
  private ResponseEntity<TransactionDTO> transferAmount(@RequestBody TransferRequest request) {
    return new ResponseEntity<>(transactionService.transfer(request), HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<TransactionDTO>> getTransactions(){
    return new ResponseEntity<>(transactionService.getTransactions(), HttpStatus.OK);
  }
}
