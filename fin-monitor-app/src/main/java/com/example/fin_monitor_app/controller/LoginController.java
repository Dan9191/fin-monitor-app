package com.example.fin_monitor_app.controller;

import com.example.fin_monitor_app.model.UserLoginResult;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.LoginUserView;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping
    public String showLoginForm(@RequestParam(required = false) Boolean error, Model model) {
        if (error != null && error) {
            model.addAttribute("errorMessage", "Неверный логин или пароль");
        }
        model.addAttribute("loginUser", new LoginUserView());
        return "login";
    }

}
