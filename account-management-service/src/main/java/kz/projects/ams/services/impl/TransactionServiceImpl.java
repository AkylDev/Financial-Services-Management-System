package kz.projects.ams.services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.TransactionRequest;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.TransactionMapper;
import kz.projects.ams.model.Account;
import kz.projects.ams.model.Transaction;
import kz.projects.ams.model.TransactionType;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.repositories.TransactionRepository;
import kz.projects.ams.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final AccountRepository accountRepository;

  private final TransactionRepository transactionRepository;

  private final TransactionMapper transactionMapper;

  @Override
  public TransactionDTO deposit(TransactionRequest request) {
    Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());

    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account Not Found!");
    }

    Account account = accountOptional.get();
    account.setBalance(account.getBalance() + request.getAmount());
    accountRepository.save(account);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAmount(request.getAmount());
    transaction.setDate(new Date());

    return transactionMapper.toDto(transactionRepository.save(transaction));
  }

  @Override
  public TransactionDTO withdraw(TransactionRequest request) {
    Optional<Account> accountOptional = accountRepository.findById(request.getAccountId());

    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account Not Found!");
    }

    Account account = accountOptional.get();
    account.setBalance(account.getBalance() - request.getAmount());
    accountRepository.save(account);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAmount(request.getAmount());
    transaction.setDate(new Date());
    return transactionMapper.toDto(transactionRepository.save(transaction));
  }

  @Override
  public Transaction transfer() {
    return null;
  }
}
