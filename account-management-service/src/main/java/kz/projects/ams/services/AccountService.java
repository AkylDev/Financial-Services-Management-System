package kz.projects.ams.services;

import kz.projects.ams.model.Account;

import java.util.List;

public interface AccountService {
  Account createAccount();

  List<Account> findByUserId(Long userId);
}
