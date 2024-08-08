package kz.projects.ams.services;

import kz.projects.ams.dto.AccountDTO;
import kz.projects.ams.models.User;

import java.util.List;

public interface AccountService {
  AccountDTO createAccount(AccountDTO account);

  List<AccountDTO> findAccountsByUserId();

  AccountDTO updateAccount(Long id, AccountDTO request);

  void deleteAccount(Long id);

  /*
  * It's quite debatable to work with User in any form within AccountService, 
  * as it violates the Single Responsibility Principle (the first principle of SOLID). 
  * It would be better to move the retrieval of the current user session to UserService.
  */
  User getCurrentSessionUser();
}
