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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/registration")
@RequiredArgsConstructor
@Slf4j
public class CreateUserController {

    /**
     * Сервис по работе с пользователями.
     */
    private final UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new CreateUserView());
        return "/registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") CreateUserView userView,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "/registration";
        }

        UserCreationResult result = userService.createUser(userView);

        if (result.isCreated()) {
            String message = String.format("Пользователь '%s' создан.",userView.getLogin());
            redirectAttributes.addFlashAttribute("message", message);
        } else {
            String message = String.format(
                    "Пользователь '%s' не создан, причина: '%s'.",
                    userView.getLogin(),
                    result.errorMessage()
            );
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }
        return "redirect:/registration";
    }
}
