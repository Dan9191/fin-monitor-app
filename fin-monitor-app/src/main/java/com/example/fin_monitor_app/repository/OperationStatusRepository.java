package com.example.fin_monitor_app.repository;

import com.example.fin_monitor_app.entity.OperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationStatusRepository extends JpaRepository<OperationStatus, Long> {
}
