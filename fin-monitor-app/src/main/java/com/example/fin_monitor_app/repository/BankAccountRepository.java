package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
}
