package kz.projects.ams.services;

import kz.projects.ams.model.Transaction;

public interface TransactionService {
  Transaction deposit();

  Transaction withdraw();

  Transaction transfer();
}
