package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.repository.BankAccountRepository;
import com.example.fin_monitor_app.service.FinTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/account/operations")
@RequiredArgsConstructor
@Slf4j
public class FinOperationController {

    private final FinTransactionService finTransactionService;

    private final BankAccountRepository bankAccountRepository;

    @GetMapping
    public String showTransactions(RedirectAttributes redirectAttributes, Model model) {
        String accountName = (String) redirectAttributes.getAttribute("bankAccountName");
        BankAccount bankAccount = bankAccountRepository.findByAccountName(accountName);
        List<FinTransaction> transactions = finTransactionService.getFinTransactionsByBankAccount(bankAccount);
        log.info("Вызван метод поиска фин операций");
        model.addAttribute("finTransactions", transactions);
        return "account/fin-operations";
    }
}