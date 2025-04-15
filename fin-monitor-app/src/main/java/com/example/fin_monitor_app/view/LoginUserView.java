package com.example.fin_monitor_app.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель для авторизации пользователя.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserView {

    private String login;
    private String password;

}
