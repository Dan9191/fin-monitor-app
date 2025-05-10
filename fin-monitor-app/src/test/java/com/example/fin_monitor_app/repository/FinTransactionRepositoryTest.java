package com.example.fin_monitor_app.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class FinTransactionRepositoryTest {

    @Mock
    private FinTransactionRepository finTransactionRepository;

    private User user;
    private BankAccount bankAccount;
    private FinTransaction finTransaction;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");

        bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAccountName("Test Account");
        bankAccount.setUser(user);

        finTransaction = new FinTransaction();
        finTransaction.setId(1);
        finTransaction.setBankAccount(bankAccount);
        finTransaction.setSum(BigDecimal.valueOf(100));
        finTransaction.setCreateDate(LocalDateTime.now());
    }

    //протестировала метод findAllByBankAccount, который возвращает страницу транзакций для счета
    @Test
    void findAllByBankAccount_Success() {
        Page<FinTransaction> transactionsPage = new PageImpl<>(List.of(finTransaction));

        when(finTransactionRepository.findAllByBankAccount(bankAccount, PageRequest.of(0, 5))).thenReturn(transactionsPage);

        Page<FinTransaction> result = finTransactionRepository.findAllByBankAccount(bankAccount, PageRequest.of(0, 5));

        assertEquals(1, result.getContent().size());
        assertEquals(finTransaction, result.getContent().get(0));
    }

    //протестировала метод findFinTransactionsByUserAndFilters, который возвращает список транзакций по фильтрам.
    @Test
    void findFinTransactionsByUserAndFilters_Success() {
        List<FinTransaction> transactions = new ArrayList<>();
        transactions.add(finTransaction);

        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now().minusDays(7);

        when(finTransactionRepository.findFinTransactionsByUserAndFilters(
                user,
                "Все",
                "Все",
                "Все",
                fromDate,
                toDate,
                0.0,
                1000.0
        )).thenReturn(transactions);

        List<FinTransaction> result = finTransactionRepository.findFinTransactionsByUserAndFilters(
                user,
                "Все",
                "Все",
                "Все",
                fromDate,
                toDate,
                0.0,
                1000.0
        );

        assertEquals(1, result.size());
        assertEquals(finTransaction, result.getFirst());
    }

    //протестировала метод findByBankAccountAndCreateDateBetween, который возвращает список транзакций для счета за указанный период.
    @Test
    void findByBankAccountAndCreateDateBetween_Success() {
        List<FinTransaction> transactions = new ArrayList<>();
        transactions.add(finTransaction);

        when(finTransactionRepository.findByBankAccountAndCreateDateBetween(
                bankAccount,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        )).thenReturn(transactions);

        List<FinTransaction> result = finTransactionRepository.findByBankAccountAndCreateDateBetween(
                bankAccount,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );

        assertEquals(1, result.size());
        assertEquals(finTransaction, result.get(0));
    }

    //протестировала метод findByUserAndCreateDateBetween, который возвращает список транзакций для пользователя за указанный период.
    @Test
    void findByUserAndCreateDateBetween_Success() {
        List<FinTransaction> transactions = new ArrayList<>();
        transactions.add(finTransaction);

        when(finTransactionRepository.findByUserAndCreateDateBetween(
                user,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        )).thenReturn(transactions);

        List<FinTransaction> result = finTransactionRepository.findByUserAndCreateDateBetween(
                user,
                LocalDateTime.now().minusDays(7),
                LocalDateTime.now()
        );

        assertEquals(1, result.size());
        assertEquals(finTransaction, result.get(0));
    }
}