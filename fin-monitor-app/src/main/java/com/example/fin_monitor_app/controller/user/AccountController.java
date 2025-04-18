package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.PersonTypeEnum;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.service.cache.PersonTypeCacheService;
import com.example.fin_monitor_app.view.CreateBankAccountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserService userService;

    private final BankAccountService bankAccountService;

    private final PersonTypeCacheService personTypeCacheService;

    private final FinTransactionService finTransactionService;


    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User user = userService.findByLogin(principal.getName());
        List<BankAccount> accounts = bankAccountService.getBankAccounts(user);
        List<FinTransaction> finTransactions = finTransactionService.getFinTransactionsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("bankAccounts", accounts);
        model.addAttribute("finTransactions", finTransactions);
        model.addAttribute("createBankAccountDto", new CreateBankAccountDto());
        return "account/dashboard";
    }

    @PostMapping("/create-account")
    public String createAccount(
            @ModelAttribute CreateBankAccountDto createBankAccountDto,
            Principal principal) {

        User user = userService.findByLogin(principal.getName());
        // Get the enum directly from DTO (no need for fromId conversion)
        PersonTypeEnum personTypeEnum = createBankAccountDto.getPersonType();

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setAccountName(createBankAccountDto.getBankAccountName());
        // Store both the enum and cached type if needed
        account.setPersonType(personTypeCacheService.findById(personTypeEnum.getId()));
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(createBankAccountDto.getBalance() != null ?
                createBankAccountDto.getBalance() : BigDecimal.ZERO);

        bankAccountService.save(account);
        return "redirect:/account/dashboard";
    }

    private String generateAccountNumber() {
        // Генерация номера счета (реализуйте по своему усмотрению)
        return "ACC" + System.currentTimeMillis();
    }
}
