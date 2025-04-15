package com.example.fin_monitor_app.service.cache;

import com.example.fin_monitor_app.entity.OperationStatus;
import com.example.fin_monitor_app.repository.OperationStatusRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, кеширующий данные по статусам операций.
 */
@Service
@RequiredArgsConstructor
public class OperationStatusCacheService {

    private final OperationStatusRepository operationStatusRepository;

    private Map<Integer, OperationStatus> operationStatuses;

    @PostConstruct
    void init() {
        operationStatuses = operationStatusRepository.findAll().stream().collect(Collectors.toMap(OperationStatus::getId, Function.identity()));
    }

    public OperationStatus findById(int id) {
        return operationStatuses.get(id);
    }
}
