package kz.projects.ams.controllers;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.TransactionRequest;
import kz.projects.ams.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

  private final TransactionService transactionService;

  @PostMapping("/deposit")
  public ResponseEntity<TransactionDTO> deposit(@RequestBody TransactionRequest request){
    return new ResponseEntity<>(transactionService.deposit(request), HttpStatus.OK);
  }

  @PostMapping("/withdraw")
  private ResponseEntity<TransactionDTO> withdrawal(@RequestBody TransactionRequest request){
    return new ResponseEntity<>(transactionService.withdraw(request), HttpStatus.OK);
  }
}
