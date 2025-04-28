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
import com.example.fin_monitor_app.view.TransactionFilterDto;
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
import java.util.Optional;

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
     * @param createFinTransactionDto Модель с данными операции.
     */
    public void save(CreateFinTransactionDto createFinTransactionDto) {
        FinTransaction finTransaction = new FinTransaction();
        Optional<BankAccount> bankAccount = bankAccountRepository.findById(createFinTransactionDto.getBankAccountId());
        if (bankAccount.isEmpty()) {
            log.error("BankAccount is name {} not found", createFinTransactionDto.getBankAccountName());
            throw new NoSuchElementException("Кошелек не найден: " + createFinTransactionDto.getBankAccountName());
        }

        finTransaction.setBankAccount(bankAccount.get());
        finTransaction.setCategory(categoryCacheService.findById(createFinTransactionDto.getCategoryEnum().getId()));
        if(createFinTransactionDto.getTransactionType().getLabel().equals("Списание")) {
            finTransaction.setSum(createFinTransactionDto.getBalance().negate());
        }
        else
            finTransaction.setSum(createFinTransactionDto.getBalance());
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
        log.info("save fin transaction: {} for account {} ", finTransaction.getId(), bankAccount.get().getAccountName());
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
    public List<FinTransaction> getFinTransactionsByPeriod(User user, LocalDateTime startDate, LocalDateTime endDate) {
        return finTransactionRepository.findByUserAndCreateDateBetween(user,startDate, endDate);
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
            TransactionFilterDto filter,
            int page, int size
    ) {
        Specification<FinTransaction> spec = Specification.where(
                FinTransactionSpecifications.byBankAccountIds(bankAccountIds)
        );
        LocalDateTime dateFrom = filter.getDateFrom() != null ? filter.getDateFrom().atStartOfDay() : null;
        if (dateFrom != null) {
            spec = spec.and(FinTransactionSpecifications.dateFrom(dateFrom));
        }

        LocalDateTime dateTo = filter.getDateTo() != null ? filter.getDateTo().plusDays(1).atStartOfDay() : null;

        if (dateTo != null) {
            spec = spec.and(FinTransactionSpecifications.dateTo(dateTo));
        }
        if (filter.getAmountFrom() != null) {
            spec = spec.and(FinTransactionSpecifications.amountFrom(filter.getAmountFrom()));
        }

        if (filter.getAmountTo() != null) {
            spec = spec.and(FinTransactionSpecifications.amountTo(filter.getAmountTo()));
        }

        if (filter.getStatusIds() != null && !filter.getStatusIds().isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.hasStatusIds(filter.getStatusIds()));
        }

        if (filter.getCategoryIds() != null && !filter.getCategoryIds().isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.hasCategoryIds(filter.getCategoryIds()));
        }

        if (filter.getTransactionTypeIds() != null && !filter.getTransactionTypeIds().isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.hasTransactionTypeIds(filter.getTransactionTypeIds()));
        }

        if (filter.getSenderBank() != null && !filter.getSenderBank().isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.bySenderBank(filter.getSenderBank()));
        }
        if (filter.getRecipientBank() != null && !filter.getRecipientBank().isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.byRecipientBank(filter.getRecipientBank()));
        }
        if (filter.getRecipientTin() != null && !filter.getRecipientTin().isEmpty()) {
            spec = spec.and(FinTransactionSpecifications.byRecipientTin(filter.getRecipientTin()));
        }

        return finTransactionRepository.findAll(spec, PageRequest.of(page, size));
    }

}
