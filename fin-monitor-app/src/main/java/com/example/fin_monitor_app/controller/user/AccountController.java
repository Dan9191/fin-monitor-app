package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserService userService;

    @GetMapping("/dashboard")
    public String accountPage(Principal principal, Model model) {
        String username = principal.getName();
        log.info("Loading dashboard for user: {}", username);

        User user = userService.findByLogin(username);

        model.addAttribute("user", user);
        return "account/dashboard";
    }
}
