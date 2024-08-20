package kz.projects.ams.services;

import kz.projects.ams.dto.AccountDTO;

import java.util.List;

public interface AccountService {
  AccountDTO createAccount(AccountDTO account);

  List<AccountDTO> findAccountsByUserId();

  AccountDTO updateAccount(Long id, AccountDTO request);

  void deleteAccount(Long id);
}
