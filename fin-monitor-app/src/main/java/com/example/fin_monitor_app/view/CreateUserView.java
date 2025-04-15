package com.example.fin_monitor_app.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Модель для передачи данных при редактировании ссылки.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserView {

    private String login;
    private String password;

}
