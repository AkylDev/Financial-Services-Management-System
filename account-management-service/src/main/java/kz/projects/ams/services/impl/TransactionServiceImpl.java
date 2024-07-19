package kz.projects.ams.services.impl;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.dto.requests.TransactionRequest;
import kz.projects.ams.dto.requests.TransferRequest;
import kz.projects.ams.exceptions.UserAccountNotFoundException;
import kz.projects.ams.mapper.TransactionMapper;
import kz.projects.ams.models.Account;
import kz.projects.ams.models.Transaction;
import kz.projects.ams.models.enums.TransactionType;
import kz.projects.ams.models.User;
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
    Optional<Account> accountOptional = accountRepository.findById(request.accountId());

    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account Not Found!");
    }

    Account account = accountOptional.get();

    if (!account.getUser().getId().equals(accountService.getCurrentSessionUser().getId())){
      throw new IllegalArgumentException("You are not allowed");
    }

    account.setBalance(account.getBalance() + request.amount());
    accountRepository.save(account);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.DEPOSIT);
    transaction.setAmount(request.amount());
    transaction.setDate(new Date());

    Transaction savedTransaction = transactionRepository.save(transaction);
    return transactionMapper.toDto(savedTransaction);
  }

  @Override
  public TransactionDTO withdraw(TransactionRequest request) {
    Optional<Account> accountOptional = accountRepository.findById(request.accountId());

    if (accountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account Not Found!");
    }

    Account account = accountOptional.get();

    if (!account.getUser().getId().equals(accountService.getCurrentSessionUser().getId())){
      throw new IllegalArgumentException("You are not allowed");
    }

    account.setBalance(account.getBalance() - request.amount());
    accountRepository.save(account);

    Transaction transaction = new Transaction();
    transaction.setAccount(account);
    transaction.setType(TransactionType.WITHDRAWAL);
    transaction.setAmount(request.amount());
    transaction.setDate(new Date());

    Transaction savedTransaction = transactionRepository.save(transaction);
    return transactionMapper.toDto(savedTransaction);
  }

  @Override
  public TransactionDTO transfer(TransferRequest request) {
    Optional<Account> fromAccountOptional = accountRepository.findById(request.fromAccount());
    Optional<Account> toAccountOptional = accountRepository.findById(request.toAccount());

    if (fromAccountOptional.isEmpty() || toAccountOptional.isEmpty()){
      throw new UserAccountNotFoundException("Account not found");
    }

    Account fromAccount = fromAccountOptional.get();

    if (!fromAccount.getUser().getId().equals(accountService.getCurrentSessionUser().getId())){
      throw new IllegalArgumentException("You are not allowed");
    }

    fromAccount.setBalance(fromAccount.getBalance() - request.amount());

    Account toAccount = toAccountOptional.get();
    toAccount.setBalance(toAccount.getBalance() + request.amount());

    accountRepository.save(fromAccount);
    accountRepository.save(toAccount);

    Transaction transaction = new Transaction();
    transaction.setAccount(fromAccount);
    transaction.setType(TransactionType.TRANSFER);
    transaction.setAmount(request.amount());
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
