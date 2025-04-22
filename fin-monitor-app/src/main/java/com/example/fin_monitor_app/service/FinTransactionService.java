package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.OperationStatusEnum;
import com.example.fin_monitor_app.repository.BankAccountRepository;
import com.example.fin_monitor_app.repository.FinTransactionRepository;
import com.example.fin_monitor_app.service.cache.CategoryCacheService;
import com.example.fin_monitor_app.service.cache.OperationStatusCacheService;
import com.example.fin_monitor_app.service.cache.TransactionTypeService;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис работы с финансовыми операциями.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FinTransactionService {

    private final FinTransactionRepository finTransactionRepository;

    private final BankAccountRepository bankAccountRepository;

    private final CategoryCacheService categoryCacheService;

    private final TransactionTypeService transactionTypeService;

    private final OperationStatusCacheService operationStatusCacheService;

    /**
     * Сохранение операции.
     *
     * @param createFinTransactionDto Модель с данными о операции.
     */
    public void save(CreateFinTransactionDto createFinTransactionDto) {
        FinTransaction finTransaction = new FinTransaction();
        BankAccount bankAccount = bankAccountRepository.findByAccountName(createFinTransactionDto.getBankAccountName());

        finTransaction.setBankAccount(bankAccount);
        finTransaction.setCategory(categoryCacheService.findById(createFinTransactionDto.getCategoryEnum().getId()));
        finTransaction.setSum(createFinTransactionDto.getBalance().abs());
        finTransaction.setCreateDate(LocalDateTime.now());
        finTransaction.setCommentary(createFinTransactionDto.getCommentary());
        finTransaction.setTransactionType(
                transactionTypeService.findById(createFinTransactionDto.getTransactionType().getId())
        );
        finTransaction.setOperationStatus(
                operationStatusCacheService.findById(createFinTransactionDto.getOperationStatus().getId())
        );

        finTransactionRepository.save(finTransaction);
        log.info("save fin transaction: {} for account {} ", finTransaction.getId(), bankAccount.getAccountName());
    }

    /**
     * Получение операций по кошельку.
     *
     * @param bankAccount Кошелек (банковский счет).
     * @return список транзакций.
     */
    public List<FinTransaction> getFinTransactionsByBankAccount(BankAccount bankAccount) {
        return finTransactionRepository.findAllByBankAccount(bankAccount);
    }

    /**
     * Полуцчение операций по пользователю.
     *
     * @param user Пользователь.
     * @return список операций.
     */
    public List<FinTransaction> getFinTransactionsByUser(User user) {
        return finTransactionRepository.findTransactionsByUserOrderByCreateDate(user);
    }

}
