package com.example.fin_monitor_app.model;

import com.example.fin_monitor_app.entity.User;

/**
 * Результат создания пользователя.
 */
public record UserCreationResult(boolean isCreated, User user, String errorMessage) {

    public static UserCreationResult successful(User user) {
        return new UserCreationResult(true, user, null);
    }

    public static UserCreationResult failure(String errorMessage) {
        return new UserCreationResult(false, null, errorMessage);
    }
}
