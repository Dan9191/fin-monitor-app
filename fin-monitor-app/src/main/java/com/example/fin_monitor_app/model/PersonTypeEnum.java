package com.example.fin_monitor_app.model;

import lombok.Getter;

import java.util.Arrays;

/**
 * Тип лица.
 */
@Getter
public enum PersonTypeEnum {
    INDIVIDUAL(1, "Физическое лицо"),
    LEGAL(2, "Юридическое лицо");

    private final int id;
    private final String label;

    PersonTypeEnum(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public static PersonTypeEnum fromId(int id) {
        return Arrays.stream(values())
                .filter(type -> type.id == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестный ID: " + id));
    }
}
