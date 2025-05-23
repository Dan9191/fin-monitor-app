package com.example.fin_monitor_app.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Статус операции.
 */
@Getter
public enum OperationStatusEnum {
    NEW(1, "Новая"),
    CONFIRMED(2, "Подтвержденная"),
    PROCESSING(3, "В обработке"),
    CANCELLED(4, "Отменена"),
    COMPLETED(5, "Платеж выполнен"),
    DELETED(6, "Платеж удален"),
    REFUND(7, "Возврат");

    private final int id;
    private final String label;

    OperationStatusEnum(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static final List<OperationStatusEnum> NON_REMOVABLE_OPERATION = Stream.of(
            CONFIRMED,
            PROCESSING,
            CANCELLED,
            COMPLETED,
            REFUND
    ).collect(Collectors.toList());

    public static OperationStatusEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(status -> status.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный ID: " + id));
    }
}
