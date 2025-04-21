package com.example.fin_monitor_app.model;

import com.example.fin_monitor_app.entity.User;

/**
 * Результат создания пользователя.
 */
public record UserLoginResult(boolean isCreated, User user, String errorMessage) {

    public static UserLoginResult success(User user) {
        return new UserLoginResult(true, user,"Ошибка");
    }

    public static UserLoginResult failure(String errorMessage) {
        return new UserLoginResult(false, null, errorMessage);
    }
}
