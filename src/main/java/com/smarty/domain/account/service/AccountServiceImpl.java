package com.smarty.domain.account.service;

import com.smarty.domain.account.entity.Account;
import com.smarty.domain.account.repository.AccountRepository;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public void validateEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new ConflictException("Account with email %s already exists".formatted(email));
        }
    }

}
