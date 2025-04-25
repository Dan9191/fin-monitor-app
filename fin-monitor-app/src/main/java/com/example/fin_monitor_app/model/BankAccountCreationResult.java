package com.example.fin_monitor_app.model;

import com.example.fin_monitor_app.entity.User;

/**
 * Результат создания пользователя.
 */
public record BankAccountCreationResult(boolean isCreated, String errorMessage) {

    public static BankAccountCreationResult successful() {
        return new BankAccountCreationResult(true, null);
    }

    public static BankAccountCreationResult failure(String errorMessage) {
        return new BankAccountCreationResult(false, errorMessage);
    }
}
