package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionalTypeRepository extends JpaRepository<TransactionType, Long> {
}
