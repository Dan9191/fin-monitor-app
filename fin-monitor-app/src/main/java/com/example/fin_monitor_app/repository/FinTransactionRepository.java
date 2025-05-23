package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FinTransactionRepository extends
        JpaRepository<FinTransaction, Long>, JpaSpecificationExecutor<FinTransaction> {

    Page<FinTransaction> findAllByBankAccount(BankAccount bankAccount, Pageable pageable);

    @Query("SELECT t FROM FinTransaction t WHERE " +
            "t.bankAccount.user = :user "
            + " AND (t.createDate BETWEEN :startDate AND :endDate) "
            + " AND (t.sum BETWEEN :minTransactionAmount AND :maxTransactionAmount) "
            + " AND (:bankAccountNumber = 'Все' OR t.bankAccount.accountNumber = :bankAccountNumber) "
            + " AND (:category = 'Все' OR t.category.name = :category) "
            + " AND (:transactionType = 'Все' OR t.transactionType.name = :transactionType) "

    )
    List<FinTransaction> findFinTransactionsByUserAndFilters(
            @Param("user") User user,
            @Param("bankAccountNumber") String bankAccountNumber,
            @Param("category") String category,
            @Param("transactionType") String transactionType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("minTransactionAmount") Double minTransactionAmount,
            @Param("maxTransactionAmount") Double maxTransactionAmount);

    List<FinTransaction> findByBankAccountAndCreateDateBetween(BankAccount account, LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT t FROM FinTransaction t WHERE t.bankAccount.user = :user AND t.createDate BETWEEN :startDate AND :endDate")
    List<FinTransaction> findByUserAndCreateDateBetween(
            @Param("user") User user,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}
