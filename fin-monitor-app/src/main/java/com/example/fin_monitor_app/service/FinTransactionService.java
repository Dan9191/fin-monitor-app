package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.repository.FinTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FinTransactionService {

    private final FinTransactionRepository finTransactionRepository;

    public void save(FinTransaction finTransaction) {
        finTransactionRepository.save(finTransaction);
    }

    public List<FinTransaction> getFinTransactionsByBankAccount(BankAccount bankAccount) {
        return finTransactionRepository.findAllByBankAccount(bankAccount);
    }

    public List<FinTransaction> getFinTransactionsByUser(User user) {
        return finTransactionRepository.findTransactionsByUserOrderByCreateDate(user);
    }
}
