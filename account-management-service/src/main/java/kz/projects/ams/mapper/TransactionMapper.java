package kz.projects.ams.mapper;

import kz.projects.ams.dto.TransactionDTO;
import kz.projects.ams.models.Transaction;
import org.springframework.stereotype.Component;


@Component
public class TransactionMapper {
  public TransactionDTO toDto(Transaction transaction) {
    if (transaction == null) {
      return null;
    }

    return new TransactionDTO(
            transaction.getId(),
            transaction.getAccount().getId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getDate()
    );
  }
}

