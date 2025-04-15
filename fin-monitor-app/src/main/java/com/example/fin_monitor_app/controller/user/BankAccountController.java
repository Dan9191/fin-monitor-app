package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.PersonType;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.service.cache.PersonTypeCacheService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final UserService userService;
    private final PersonTypeCacheService personTypeService;
    private final BankAccountService bankAccountService;

    @GetMapping()
    public String showCreateForm(HttpServletRequest request, Model model) {
        // Получаем логин из куки
        String login = getLoginFromCookies(request);
        if (login == null) {
            return "redirect:/login"; // перенаправляем на страницу входа, если нет куки
        }

        // Находим пользователя по логину
        User user = userService.findByLogin(login);
        if (user == null) {
            return "redirect:/login"; // перенаправляем, если пользователь не найден
        }

        // Получаем все типы лиц для выпадающего списка
        List<PersonType> personTypes = personTypeService.findAll();

        model.addAttribute("user", user.getLogin());
        model.addAttribute("personTypes", personTypes);
        model.addAttribute("bankAccount", new BankAccount());

        return "bank-account/create";
    }

    @PostMapping("/create")
    public String createAccount(@ModelAttribute BankAccount bankAccount,
                                HttpServletRequest request) {
        String login = getLoginFromCookies(request);
        if (login == null) {
            return "redirect:/login";
        }

        User user = userService.findByLogin(login);
        if (user == null) {
            return "redirect:/login";
        }

        // Устанавливаем пользователя и начальный баланс (0)
        bankAccount.setUser(user);
        // Если баланс не указан, устанавливаем 0
        if (bankAccount.getBalance() == null) {
            bankAccount.setBalance(BigDecimal.ZERO);
        }

        bankAccountService.save(bankAccount);

        return "redirect:/bank-accounts"; // перенаправляем на список счетов
    }

    private String getLoginFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("login".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
