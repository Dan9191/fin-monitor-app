package com.example.fin_monitor_app.controller.user;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.BankAccountCreationResult;
import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.model.OperationStatusEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.CreateBankAccountDto;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import com.example.fin_monitor_app.view.TransactionFilterDto;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.fin_monitor_app.model.OperationStatusEnum.DELETED;
import static com.example.fin_monitor_app.model.TransactionTypeEnum.INCOME;
import static com.example.fin_monitor_app.model.TransactionTypeEnum.OUTCOME;

@ExtendWith(MockitoExtension.class)
class AccountControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private FinTransactionService finTransactionService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private AccountController accountController;

    private User user;
    private BankAccount bankAccount;
    private FinTransaction finTransaction;
    private CreateBankAccountDto createBankAccountDto;
    private CreateFinTransactionDto createFinTransactionDto;
    private TransactionFilterDto filter;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("testUser");

        bankAccount = new BankAccount();
        bankAccount.setId(1);
        bankAccount.setAccountName("Test Account");
        bankAccount.setBalance(BigDecimal.ZERO);

        finTransaction = new FinTransaction();
        finTransaction.setId(1);
        finTransaction.setBankAccount(bankAccount);
        finTransaction.setSum(BigDecimal.valueOf(100));
        finTransaction.setCreateDate(LocalDateTime.now());

        createBankAccountDto = new CreateBankAccountDto();
        createBankAccountDto.setBankAccountName("Test Account");
        createBankAccountDto.setBalance(BigDecimal.ZERO);

        createFinTransactionDto = new CreateFinTransactionDto();
        createFinTransactionDto.setBankAccountId(1L);
        createFinTransactionDto.setBalance(BigDecimal.valueOf(100));
        createFinTransactionDto.setCommentary("Test transaction");

        filter = new TransactionFilterDto();
    }

    //тестировала метод createFinTransaction, который создает новую транзакцию
    @Test
    void createFinTransaction_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(finTransactionService).save(createFinTransactionDto);

        String result = accountController.createFinTransaction(createFinTransactionDto, request);

        assertEquals("redirect:/account/dashboard", result);
        verify(finTransactionService).save(createFinTransactionDto);
    }
}