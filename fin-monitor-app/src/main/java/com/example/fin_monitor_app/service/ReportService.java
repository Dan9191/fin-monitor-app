package com.example.fin_monitor_app.service;

import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import com.example.fin_monitor_app.repository.FinTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Сервис работы с финансовой отчётностью.
 */
@Service
@RequiredArgsConstructor
public class ReportService {
    private final FinTransactionRepository finTransactionRepository;

    public List<FinTransaction> getFilteredTransactions(
            User user,
            String bankAccountNumber,
            String category,
            String transactionType,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        return finTransactionRepository.findFinTransactionsByUserAndFilters(
                user,
                bankAccountNumber,
                category,
                transactionType,
                startDate,
                endDate
        );
    }
}