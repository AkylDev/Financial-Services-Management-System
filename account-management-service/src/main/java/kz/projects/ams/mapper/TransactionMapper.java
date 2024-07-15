package kz.projects.ams.mapper;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.models.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

  public TransactionDTO toDto(Transaction transaction){
    if (transaction == null){
      return null;
    }

    TransactionDTO transactionDTO = new TransactionDTO();
    transactionDTO.setId(transaction.getId());
    transactionDTO.setAccountId(transaction.getAccount().getId());
    transactionDTO.setType(transaction.getType());
    transactionDTO.setAmount(transaction.getAmount());
    transactionDTO.setDate(transaction.getDate());

    return transactionDTO;
  }

}
