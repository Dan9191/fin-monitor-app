package com.example.fin_monitor_app.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class BankAccountRepositoryTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    private User user;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Test User");

        bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAccountName("Test Account");
        bankAccount.setUser(user);
    }

    // протестировала метод findAllByUser, который возвращает список счетов для пользователя.
    @Test
    void findAllByUser_Success() {
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(bankAccount);

        when(bankAccountRepository.findAllByUser(user)).thenReturn(accounts);

        List<BankAccount> result = bankAccountRepository.findAllByUser(user);

        assertEquals(1, result.size());
        assertEquals(bankAccount, result.get(0));
    }

    //протестировала метод findByAccountNameAndUser, который возвращает счет по имени и пользователю.
    @Test
    void findByAccountNameAndUser_Success() {
        when(bankAccountRepository.findByAccountNameAndUser("Test Account", user)).thenReturn(bankAccount);

        BankAccount result = bankAccountRepository.findByAccountNameAndUser("Test Account", user);

        assertEquals(bankAccount, result);
    }
}