package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис по работе с счетами.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public void save(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }

    public List<BankAccount> getBankAccounts(User user) {
        return bankAccountRepository.findAllByUser(user);
    }
}
