package kz.projects.ams.services;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.TransactionRequest;
import kz.projects.ams.dto.TransferRequest;

import java.util.List;

public interface TransactionService {
  TransactionDTO deposit(TransactionRequest request);

  TransactionDTO withdraw(TransactionRequest request);

  TransactionDTO transfer(TransferRequest request);

  List<TransactionDTO> getTransactions();
}
