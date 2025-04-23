package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FinTransactionRepository extends JpaRepository<FinTransaction, Long> {

    List<FinTransaction> findAllByBankAccount(BankAccount bankAccount);

    @Query("SELECT t FROM FinTransaction t WHERE t.bankAccount.user = :user ORDER BY t.createDate DESC")
    List<FinTransaction> findTransactionsByUserOrderByCreateDate(@Param("user") User user);

    @Query("SELECT t FROM FinTransaction t WHERE " +
            "t.bankAccount.user = :user "
            + " AND (t.createDate BETWEEN :startDate AND :endDate) "
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
            @Param("endDate") LocalDateTime endDate);
}
