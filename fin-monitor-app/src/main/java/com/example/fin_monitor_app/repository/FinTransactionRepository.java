package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FinTransactionRepository extends JpaRepository<FinTransaction, Long> {

    List<FinTransaction> findAllByBankAccount(BankAccount bankAccount);

    @Query("SELECT t FROM FinTransaction t WHERE t.bankAccount.user = :user ORDER BY t.createDate DESC")
    List<FinTransaction> findTransactionsByUserOrderByCreateDate(@Param("user") User user);
}
