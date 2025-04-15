package com.example.fin_monitor_app.model;

/**
 * Результат создания пользователя.
 */
public record UserLoginResult(boolean isCreated, String errorMessage) {

    public static UserLoginResult successful() {
        return new UserLoginResult(true,null);
    }

    public static UserLoginResult failure(String errorMessage) {
        return new UserLoginResult(false, errorMessage);
    }
}
