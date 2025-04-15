package com.example.fin_monitor_app.controller;

import com.example.fin_monitor_app.model.UserCreationResult;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.CreateUserView;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class CreateUserController {

    /**
     * Сервис по работе с пользователями.
     */
    private final UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new CreateUserView());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") CreateUserView userView,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "register";
        }
        UserCreationResult result = userService.createUser(userView);

        if (result.isCreated()) {
            String message = String.format("Пользователь '%s' создан.",userView.getLogin());
            model.addAttribute("message", message);
        } else {
            String message = String.format(
                    "Пользователь '%s' не создан, причина: '%s'.",
                    userView.getLogin(),
                    result.errorMessage()
            );
            model.addAttribute("errorMessage", message);
        }
        return "register";
    }

}
