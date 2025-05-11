package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.TransactionType;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.OperationStatusEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import com.example.fin_monitor_app.repository.BankAccountRepository;
import com.example.fin_monitor_app.repository.FinTransactionRepository;
import com.example.fin_monitor_app.service.cache.CategoryCacheService;
import com.example.fin_monitor_app.service.cache.OperationStatusCacheService;
import com.example.fin_monitor_app.service.cache.TransactionTypeService;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import com.example.fin_monitor_app.view.EditFinTransactionDto;
import com.example.fin_monitor_app.utils.FinTransactionSpecifications;
import com.example.fin_monitor_app.view.ReportFilterDto;
import com.example.fin_monitor_app.view.TransactionFilterDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
        Optional<BankAccount> bankAccountOpt = bankAccountRepository.findById(createFinTransactionDto.getBankAccountId());
        if (bankAccountOpt.isEmpty()) {
            log.error("BankAccount is name {} not found", createFinTransactionDto.getBankAccountName());
            throw new NoSuchElementException("Кошелек не найден: " + createFinTransactionDto.getBankAccountName());
        }

        BankAccount bankAccount = bankAccountOpt.get();

        // Обновление баланса в зависимости от типа транзакции
        BigDecimal currentBalance = bankAccount.getBalance();
        BigDecimal transactionAmount = createFinTransactionDto.getBalance().abs();
        if (createFinTransactionDto.getTransactionType() == TransactionTypeEnum.INCOME) {
            bankAccount.setBalance(currentBalance.add(transactionAmount));
        } else if (createFinTransactionDto.getTransactionType() == TransactionTypeEnum.OUTCOME) {
            bankAccount.setBalance(currentBalance.subtract(transactionAmount));
        }

        finTransaction.setBankAccount(bankAccount);
        finTransaction.setCategory(categoryCacheService.findById(createFinTransactionDto.getCategoryEnum().getId()));
        finTransaction.setSum(transactionAmount);
        finTransaction.setCreateDate(LocalDateTime.now());
        finTransaction.setCommentary(createFinTransactionDto.getCommentary());
        finTransaction.setTransactionType(
                transactionTypeService.findById(createFinTransactionDto.getTransactionType().getId())
        );
        finTransaction.setOperationStatus(
                operationStatusCacheService.findById(createFinTransactionDto.getOperationStatus().getId())
        );

        // Дополнительные поля для операции
        finTransaction.setSenderBank(createFinTransactionDto.getSenderBank());
        finTransaction.setRecipientBank(createFinTransactionDto.getRecipientBank());
        finTransaction.setRecipientBankAccount(createFinTransactionDto.getRecipientBankAccount());
        finTransaction.setRecipientTelephoneNumber(createFinTransactionDto.getRecipientTelephoneNumber());
        finTransaction.setRecipientTin(createFinTransactionDto.getRecipientTin());
        finTransaction.setWithdrawalAccount(createFinTransactionDto.getWithdrawalAccount());

        finTransactionRepository.save(finTransaction);
        bankAccountRepository.save(bankAccount);
        log.info("save fin transaction: {} for account {} ", finTransaction.getId(), bankAccount.getAccountName());
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

        // Откат эффекта транзакции на баланс
        BankAccount bankAccount = transaction.getBankAccount();
        BigDecimal currentBalance = bankAccount.getBalance();
        BigDecimal transactionAmount = transaction.getSum();
        TransactionType transactionType = transaction.getTransactionType();

        if (transactionType.getId() == TransactionTypeEnum.INCOME.getId()) {
            if (currentBalance.compareTo(transactionAmount) < 0) {
                throw new IllegalStateException("Недостаточно средств на счете для отмены транзакции: " + bankAccount.getAccountName());
            }
            bankAccount.setBalance(currentBalance.subtract(transactionAmount));
        } else if (transactionType.getId() == TransactionTypeEnum.OUTCOME.getId()) {
            bankAccount.setBalance(currentBalance.add(transactionAmount));
        }

        OperationStatus deletedStatus = operationStatusCacheService.findById(OperationStatusEnum.DELETED.getId());
        transaction.setOperationStatus(deletedStatus);

        finTransactionRepository.save(transaction);
        bankAccountRepository.save(bankAccount);
        log.info("Transaction {} marked as deleted with updated balance {}", transactionId, bankAccount.getBalance());
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
        return finTransactionRepository.findById(transactionId)
                .orElseThrow(() -> {
                    log.error("Transaction id {} not found", transactionId);
                    return new NoSuchElementException("Транзакция не найдена: " + transactionId);
                });

    }

    /**
     * Внесение изменений в транзакцию
     *
     * @param dto dto с внесенными в транзакцию изменениями.
     */
    @Transactional
    public void update(EditFinTransactionDto dto) {
        FinTransaction finTransaction = finTransactionRepository.findById((long) dto.getId())
                .orElseThrow(() -> {
                    log.error("transaction id {} not found", dto.getId());
                    return new NoSuchElementException("Transaction not found");
                });

        BankAccount bankAccount = finTransaction.getBankAccount();

        // Отмена предыдущей изменения баланса
        BigDecimal currentBalance = bankAccount.getBalance();
        BigDecimal oldAmount = finTransaction.getSum();
        TransactionType oldType = finTransaction.getTransactionType();
        if (oldType.getId() == TransactionTypeEnum.INCOME.getId()) {
            bankAccount.setBalance(currentBalance.subtract(oldAmount));
        } else if (oldType.getId() == TransactionTypeEnum.OUTCOME.getId()) {
            bankAccount.setBalance(currentBalance.add(oldAmount));
        }

        // Применение нового изменения баланса
        BigDecimal newAmount = dto.getBalance().abs();
        TransactionTypeEnum newType = dto.getTransactionType();
        if (newType == TransactionTypeEnum.INCOME) {
            bankAccount.setBalance(bankAccount.getBalance().add(newAmount));
        } else if (newType == TransactionTypeEnum.OUTCOME) {
            bankAccount.setBalance(bankAccount.getBalance().subtract(newAmount));
        }

        // Обновление полей транзакции
        finTransaction.setCategory(categoryCacheService.findById(dto.getCategoryEnum().getId()));
        finTransaction.setSum(newAmount);
        finTransaction.setCreateDate(dto.getCreateDate());
        finTransaction.setCommentary(dto.getCommentary());
        finTransaction.setTransactionType(
                transactionTypeService.findById(dto.getTransactionType().getId())
        );
        finTransaction.setOperationStatus(
                operationStatusCacheService.findById(dto.getOperationStatus().getId())
        );
        finTransaction.setSenderBank(dto.getSenderBank());
        finTransaction.setRecipientBank(dto.getRecipientBank());
        finTransaction.setRecipientBankAccount(dto.getRecipientBankAccount());
        finTransaction.setRecipientTelephoneNumber(dto.getRecipientTelephoneNumber());
        finTransaction.setRecipientTin(dto.getRecipientTin());
        finTransaction.setWithdrawalAccount(dto.getWithdrawalAccount());

        finTransactionRepository.save(finTransaction);
        bankAccountRepository.save(bankAccount);
        log.info("Transaction id: {} updated with new balance {}", dto.getId(), bankAccount.getBalance());
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

        PageRequest pageRequest = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createDate")
        );
        return finTransactionRepository.findAll(spec, pageRequest);
    }

    /**
     * Поиск записей по фильтрам + пагинация.
     */
    public List<FinTransaction> getFilteredTransactions(ReportFilterDto filter
    ) {
        Specification<FinTransaction> spec = Specification.where(
                FinTransactionSpecifications.byBankAccountIds(filter.getBankAccountIds())
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

        return finTransactionRepository.findAll(spec);
    }


}
