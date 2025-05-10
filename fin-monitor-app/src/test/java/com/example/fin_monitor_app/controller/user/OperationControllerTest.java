package com.example.fin_monitor_app.controller.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class OperationControllerTest {

    @Mock
    private FinTransactionService finTransactionService;

    @InjectMocks
    private OperationController operationController;

    private CreateFinTransactionDto createFinTransactionDto;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setLogin("testUser");

        BankAccount bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAccountName("Test Account");
        bankAccount.setBalance(BigDecimal.ZERO);

        FinTransaction finTransaction = new FinTransaction();
        finTransaction.setId(1);
        finTransaction.setBankAccount(bankAccount);
        finTransaction.setSum(BigDecimal.valueOf(100));
        finTransaction.setCreateDate(LocalDateTime.now());

        createFinTransactionDto = new CreateFinTransactionDto();
        createFinTransactionDto.setBankAccountId(1L);
        createFinTransactionDto.setBalance(BigDecimal.valueOf(100));
        createFinTransactionDto.setCommentary("Test transaction");

    }

    @Test
    void createFinTransaction_Success() {
        doNothing().when(finTransactionService).save(createFinTransactionDto);

        String result = operationController.createFinTransaction(createFinTransactionDto, 1);

        assertEquals("redirect:/operations/1", result);
        verify(finTransactionService).save(createFinTransactionDto);
    }
}