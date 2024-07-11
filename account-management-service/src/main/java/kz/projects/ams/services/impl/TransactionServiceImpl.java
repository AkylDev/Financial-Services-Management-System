package kz.projects.ams.services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.TransactionRequest;
import kz.projects.ams.dto.TransferRequest;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.TransactionMapper;
import kz.projects.ams.model.Account;
import kz.projects.ams.model.Transaction;
import kz.projects.ams.model.TransactionType;
import kz.projects.ams.model.User;
import kz.projects.ams.repositories.AccountRepository;
import kz.projects.ams.repositories.TransactionRepository;
import kz.projects.ams.services.AccountService;
import kz.projects.ams.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

  private final AccountRepository accountRepository;

  private final TransactionRepository transactionRepository;

  private final TransactionMapper transactionMapper;

  private final AccountService accountService;

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

    Transaction savedTransaction = transactionRepository.save(transaction);
    return transactionMapper.toDto(savedTransaction);
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

    Transaction savedTransaction = transactionRepository.save(transaction);
    return transactionMapper.toDto(savedTransaction);
  }

  @Override
  public TransactionDTO transfer(TransferRequest request) {
    Optional<Account> fromAccountOptional = accountRepository.findById(request.getFromAccount());
    Optional<Account> toAccountOptional = accountRepository.findById(request.getToAccount());

    if (fromAccountOptional.isEmpty() || toAccountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account fromAccount = fromAccountOptional.get();
    fromAccount.setBalance(fromAccount.getBalance() - request.getAmount());

    Account toAccount = toAccountOptional.get();
    toAccount.setBalance(toAccount.getBalance() + request.getAmount());

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);

    Transaction transaction = new Transaction();
    transaction.setAccount(fromAccount);
    transaction.setType(TransactionType.TRANSFER);
    transaction.setAmount(request.getAmount());
    transaction.setDate(new Date());

    Transaction savedTransaction = transactionRepository.save(transaction);
    return transactionMapper.toDto(savedTransaction);
  }

  @Override
  public List<TransactionDTO> getTransactions() {
    User currentUser = accountService.getCurrentSessionUser();
    List<Transaction> transactions = transactionRepository.findAllByUserId(currentUser.getId());
    return transactions.stream()
            .map(transactionMapper::toDto)
            .collect(Collectors.toList());
  }
}
