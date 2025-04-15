package com.example.fin_monitor_app.model;

import com.example.fin_monitor_app.entity.Role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * Тип пользователя.
 */
@RequiredArgsConstructor
@Getter
public enum RoleEnum {
    ADMIN(1, "Администратор"),
    USER(2, "Пользователь"),
    DELETED(3, "Удален");

    private final int id;
    private final String label;

    private static final RoleEnum[] VALUES = values();

    /**
     * Поиск значения перечисления по сущности БД.
     *
     * @param role сущность в БД
     * @return перечисление или null, если перечисление не найдено
     */
    public static RoleEnum findByRole(Role role) {
        if (role == null) {
            return null;
        }
        return Arrays.stream(VALUES)
                .filter(item -> Objects.equals(item.id, role.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Поиск значения перечисления по сущности БД.
     *
     * @param role сущность в БД
     * @return перечисление или null, если перечисление не найдено
     */
    public static RoleEnum findByRoleName(String role) {
        if (role == null) {
            return null;
        }
        return Arrays.stream(VALUES)
                .filter(item -> Objects.equals(item.name(), role))
                .findFirst()
                .orElse(null);
    }

}
