package com.example.fin_monitor_app.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.BankAccountCreationResult;
import com.example.fin_monitor_app.model.PersonTypeEnum;
import com.example.fin_monitor_app.repository.BankAccountRepository;
import com.example.fin_monitor_app.service.cache.PersonTypeCacheService;
import com.example.fin_monitor_app.view.CreateBankAccountDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private PersonTypeCacheService personTypeCacheService;

    @InjectMocks
    private BankAccountService bankAccountService;

    private CreateBankAccountDto dto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(); // инициализация юзера
        testUser.setName("Test User");

        dto = new CreateBankAccountDto();
        dto.setBankAccountName("My Account");
        dto.setPersonType(PersonTypeEnum.INDIVIDUAL); // Физическое лицо
        dto.setBalance(BigDecimal.valueOf(100));
    }

    //протестировано удаление банковского аккаунта
    @Test
    void shouldDeleteBankAccount() {
        BankAccount existingAccount = new BankAccount();
        existingAccount.setAccountName("Test Account");

        doNothing().when(bankAccountRepository).delete(existingAccount);

        bankAccountService.delete(existingAccount);

        verify(bankAccountRepository).delete(existingAccount);
    }

    //протестировано получение всех банковский счетов
    @Test
    void shouldGetListOfBankAccountsForUser() {
        when(bankAccountRepository.findAllByUser(testUser)).thenReturn(Collections.emptyList());

        List<BankAccount> accounts = bankAccountService.getBankAccounts(testUser);

        assertEquals(0, accounts.size());
        verify(bankAccountRepository).findAllByUser(testUser);
    }

    //протестировано банковского счёта по ID
    @Test
    void shouldFindBankAccountById() {
        int accountId = 1;
        BankAccount expectedAccount = new BankAccount();
        expectedAccount.setAccountName("Found Account");

        when(bankAccountRepository.findById(accountId)).thenReturn(expectedAccount);

        BankAccount actualAccount = bankAccountService.getBankAccountById(accountId);

        assertEquals(expectedAccount, actualAccount);
        verify(bankAccountRepository).findById(accountId);
    }
}
