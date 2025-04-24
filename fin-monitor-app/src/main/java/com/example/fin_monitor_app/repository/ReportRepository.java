package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<FinTransaction, Long> {
    @Query("SELECT t FROM FinTransaction t WHERE t.bankAccount.user = :user " +
            "AND (:accountNumber IS NULL OR t.bankAccount.accountNumber = :accountNumber) " +
            "AND (:category IS NULL OR t.category = :category) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:startDate IS NULL OR t.createDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.createDate <= :endDate)")
    List<FinTransaction> findFilteredTransactions(
            @Param("user") User user,
            @Param("accountNumber") String accountNumber,
            @Param("category") CategoryEnum category,
            @Param("transactionType") TransactionTypeEnum transactionType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}