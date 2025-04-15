package com.example.fin_monitor_app.service.cache;

import com.example.fin_monitor_app.entity.TransactionType;
import com.example.fin_monitor_app.repository.TransactionalTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, кеширующий данные по типу транзакции.
 */
@Service
@RequiredArgsConstructor
public class TransactionTypeService {

    private final TransactionalTypeRepository transactionalTypeRepository;

    private Map<Integer, TransactionType> transactionTypes;

    @PostConstruct
    void init() {
        transactionTypes = transactionalTypeRepository.findAll().stream().collect(Collectors.toMap(TransactionType::getId, Function.identity()));
    }

    public TransactionType findById(int id) {
        return transactionTypes.get(id);
    }
}
