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
import com.example.fin_monitor_app.view.EditFinTransactionDto;
import com.example.fin_monitor_app.utils.FinTransactionSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import com.example.fin_monitor_app.entity.OperationStatus;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.fin_monitor_app.model.OperationStatusEnum.NON_REMOVABLE_OPERATION;

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
        if (bankAccount == null) {
            log.error("BankAccount is name {} not found", createFinTransactionDto.getBankAccountName());
            throw new NoSuchElementException("Кошелек не найдена: " + createFinTransactionDto.getBankAccountName());
        }

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

        // дополнительные поля для операции
        finTransaction.setSenderBank(createFinTransactionDto.getSenderBank()); //Банк отправителя
        finTransaction.setRecipientBank(createFinTransactionDto.getRecipientBank()); //Банк получателя
        finTransaction.setRecipientBankAccount(createFinTransactionDto.getRecipientBankAccount()); //Расчетный счет получателя
        finTransaction.setRecipientTelephoneNumber(createFinTransactionDto.getRecipientTelephoneNumber());//Телефон получателя
        finTransaction.setRecipientTin(createFinTransactionDto.getRecipientTin()); // ИНН получателя
        finTransaction.setWithdrawalAccount(createFinTransactionDto.getWithdrawalAccount());//Счет списания



        finTransactionRepository.save(finTransaction);
        log.info("save fin transaction: {} for account {} ", finTransaction.getId(), bankAccount.getAccountName());
    }

    /**
     * Получение операций по кошельку.
     *
     * @param bankAccount Кошелек (банковский счет).
     * @return список транзакций.
     */
    public Page<FinTransaction> getFinTransactionsByBankAccount(BankAccount bankAccount, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createDate").descending());
        return finTransactionRepository.findAllByBankAccount(bankAccount, pageable);
    }

    /**
     * Изменение статуса операции
     *
     * @param transactionId id-изменяемой транзакции.
     */
    @Transactional
    public void markAsDeleted(Long transactionId) {
        FinTransaction transaction = finTransactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("markAsDeleted transaction id {} not found", transactionId);
                    return new NoSuchElementException("Транзакция не найдена: " + transactionId);
                });

        OperationStatusEnum statusEnum = OperationStatusEnum.fromId(transaction.getOperationStatus().getId());
        if (NON_REMOVABLE_OPERATION.contains(statusEnum)) {
            log.error("Transaction is not deletable: {} ", transactionId);
            throw new NoSuchElementException("Транзакцию в данном статусе запрещено удалять: " + transactionId);
        }

        OperationStatus deletedStatus = operationStatusCacheService.findById(OperationStatusEnum.DELETED.getId());
        transaction.setOperationStatus(deletedStatus);

        finTransactionRepository.save(transaction);
        log.info("Transaction {} marked as deleted", transactionId);
    }

    /**
     * Получение транзакций конкретного кошелька за указанный период.
     */
    public List<FinTransaction> getFinTransactionsByBankAccountAndPeriod(BankAccount account,
                                                                         LocalDateTime startDate,
                                                                         LocalDateTime endDate) {
        return finTransactionRepository.findByBankAccountAndCreateDateBetween(account, startDate, endDate);
    }

    /**
     * Получение транзакций за указанный период.
     */
    public List<FinTransaction> getFinTransactionsByPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        return finTransactionRepository.findByCreateDateBetween(startDate, endDate);
    }

    /**
     * Поиск транзакции по id
     *
     * @param transactionId id-изменяемой транзакции.
     * @return транзакция.
     */
    public FinTransaction findTransactionById(Long transactionId) {
        FinTransaction transaction = finTransactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("markAsDeleted transaction id {} not found", transactionId);
                    return new NoSuchElementException("Транзакция не найдена: " + transactionId);
                });
        return transaction;

    }

    /**
     * Внесение изменений в транзакцию
     *
     * @param dto dto с внесенными в транзакцию изменениями.
     */
    public void update(EditFinTransactionDto dto) {
        FinTransaction finTransaction = finTransactionRepository.findById((long) dto.getId())
                .orElseThrow(() -> {
                    log.error("transaction id {} not found", dto.getId());
                     return new NoSuchElementException("Transaction not found");
                });

        BankAccount bankAccount = bankAccountRepository.findByAccountName(dto.getBankAccountName());
        finTransaction.setBankAccount(bankAccount);
        finTransaction.setCategory(categoryCacheService.findById(dto.getCategoryEnum().getId()));
        finTransaction.setSum(dto.getBalance().abs());
        finTransaction.setCreateDate(LocalDateTime.now());
        finTransaction.setCommentary(dto.getCommentary());
        finTransaction.setTransactionType(
                transactionTypeService.findById(dto.getTransactionType().getId())
        );
        finTransaction.setOperationStatus(
                operationStatusCacheService.findById(dto.getOperationStatus().getId())
        );
        // дополнительные поля для операции
        finTransaction.setSenderBank(dto.getSenderBank()); //Банк отправителя
        finTransaction.setRecipientBank(dto.getRecipientBank()); //Банк получателя
        finTransaction.setRecipientBankAccount(dto.getRecipientBankAccount()); //Расчетный счет получателя
        finTransaction.setRecipientTelephoneNumber(dto.getRecipientTelephoneNumber());//Телефон получателя
        finTransaction.setRecipientTin(dto.getRecipientTin()); // ИНН получателя
        finTransaction.setWithdrawalAccount(dto.getWithdrawalAccount());//Счет списания

        finTransactionRepository.save(finTransaction);
        log.info("Transaction id: {} updated", dto.getId());
    }

    /**
     * Поиск записей по фильтрам + пагинация.
     */
    public Page<FinTransaction> getFilteredTransactions(
            List<Integer> bankAccountIds,
            List<Integer> statusIds,
            LocalDateTime dateFrom,
            LocalDateTime dateTo,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page, int size
    ) {
        Specification<FinTransaction> spec = Specification.where(
                FinTransactionSpecifications.byBankAccountIds(bankAccountIds)
        );
        if (dateFrom != null) {
            spec = spec.and(FinTransactionSpecifications.dateFrom(dateFrom));
        }

        if (dateTo != null) {
            spec = spec.and(FinTransactionSpecifications.dateTo(dateTo));
        }
        if (minAmount != null) {
            spec = spec.and(FinTransactionSpecifications.amountFrom(minAmount));
        }

        if (maxAmount != null) {
            spec = spec.and(FinTransactionSpecifications.amountTo(maxAmount));
        }

        if (statusIds != null && !statusIds.isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.hasStatusIds(statusIds));
        }

        return finTransactionRepository.findAll(spec, PageRequest.of(page, size));
    }

}
