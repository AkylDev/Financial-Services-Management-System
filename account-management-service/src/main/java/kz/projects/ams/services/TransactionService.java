package kz.projects.ams.services;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.TransactionRequest;
import kz.projects.ams.model.Transaction;

public interface TransactionService {
  TransactionDTO deposit(TransactionRequest request);

  TransactionDTO withdraw(TransactionRequest request);

  Transaction transfer();
}
