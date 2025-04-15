package com.example.fin_monitor_app.model;

import lombok.Getter;

import java.util.Arrays;

/**
 * Тип транзакции.
 */
@Getter
public enum TransactionTypeEnum {
    INCOME(1, "Поступление"),
    OUTCOME(2, "Списание");

    private final int id;
    private final String label;

    TransactionTypeEnum(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static TransactionTypeEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(type -> type.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный ID: " + id));
    }
}