package com.smarty.domain.account.service;

import com.smarty.domain.account.repository.AccountRepository;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void existsByEmail(String email) {
        if (accountRepository.existsByEmail(email)) {
            throw new ConflictException("Account with email %s already exists".formatted(email));
        }
    }

}
