package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.CreateBankAccountDto;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserService userService;

    private final BankAccountService bankAccountService;

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
        model.addAttribute("createFinTransactionDto", new CreateFinTransactionDto());
        return "account/dashboard";
    }

    @PostMapping("/create-account")
    public String createAccount(
            @ModelAttribute CreateBankAccountDto createBankAccountDto,
            Principal principal) {
        User user = userService.findByLogin(principal.getName());
        bankAccountService.save(createBankAccountDto, user);
        return "redirect:/account/dashboard";
    }

    @PostMapping("/create-fin-transaction")
    public String createFinTransaction(@ModelAttribute CreateFinTransactionDto createFinTransactionDto) {

        finTransactionService.save(createFinTransactionDto);
        return "redirect:/account/dashboard";
    }

    @GetMapping("/account/{id}/operations")
    public String showAccountOperations(@PathVariable Integer id, Model model) {
        BankAccount account = bankAccountService.getBankAccountById(id);

        if (account == null) {
            return "redirect:/account/dashboard";
        }

        List<FinTransaction> transactions = finTransactionService.getFinTransactionsByBankAccount(account);

        List<LocalDate> last30Days = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            last30Days.add(LocalDate.now().minusDays(i));
        }

        // Группировка по категориям для операций последней недели
        Map<String, BigDecimal> transactionsByCategory = transactions.stream()
                .filter(t -> last30Days.contains(t.getCreateDate().toLocalDate()))
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                FinTransaction::getSum,
                                BigDecimal::add
                        )
                ));

        model.addAttribute("finTransactions", transactions);
        model.addAttribute("transactionsByCategory", transactionsByCategory);
        return "account/fin-operations";
    }

    @PostMapping("/delete-account/{id}") // Изменили на POST
    public String deleteAccount(
            @PathVariable Integer id) {
        BankAccount account = bankAccountService.getBankAccountById(id);
        bankAccountService.delete(account);
        return "redirect:/account/dashboard";
    }

}
