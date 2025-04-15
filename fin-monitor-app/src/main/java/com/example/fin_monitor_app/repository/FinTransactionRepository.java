package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.FinTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FinTransactionRepository extends JpaRepository<FinTransaction, Long> {
}
