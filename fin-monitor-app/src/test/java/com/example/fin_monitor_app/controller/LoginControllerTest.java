package com.example.fin_monitor_app.controller;

import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.LoginUserView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        // Инициализация моков и контроллера
    }

    // проверяла, что метод showLoginForm работает без ошибок
    @Test
    void showLoginForm_WithoutError() {
        // Вызов метода контроллера без ошибки
        String result = loginController.showLoginForm(null, model);

        // Проверка результата
        assertEquals("login", result);

        // Проверка, что в модель добавлен атрибут "loginUser"
        verify(model).addAttribute("loginUser", new LoginUserView());
    }

    // проверяла, что метод showLoginForm работает корректно с ошибками
    @Test
    void showLoginForm_WithError() {
        // Вызов метода контроллера с ошибкой
        String result = loginController.showLoginForm(true, model);

        // Проверка результата
        assertEquals("login", result);

        // Проверка, что в модель добавлен атрибут "errorMessage"
        verify(model).addAttribute("errorMessage", "Неверный логин или пароль");

        // Проверка, что в модель добавлен атрибут "loginUser"
        verify(model).addAttribute("loginUser", new LoginUserView());
    }
}