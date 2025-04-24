package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.reports.HttpReport;
import com.example.fin_monitor_app.repository.FinTransactionRepository;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/account/reports")
@RequiredArgsConstructor
public class ReportController {
    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final FinTransactionRepository finTransactionRepository;

    @GetMapping
    public String reportsPage(Principal principal, Model model) {
        User user = userService.findByLogin(principal.getName());
        model.addAttribute("user", user);

        // Добавляем список кошельков пользователя
        model.addAttribute("bankAccounts", bankAccountService.getBankAccounts(user));

        return "account/reports";
    }

    @PostMapping("/generate-report")
    public void generateReport(
            Principal principal,
            @RequestParam(required = false) String bankAccountNumber,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam String fileExtension,
            HttpServletResponse response) throws IOException {

        String userName = principal.getName();
        User user = userService.findByLogin(userName);
        List<FinTransaction> finTransactionsDetails = finTransactionRepository.findFinTransactionsByUserAndFilters(
                user,
                bankAccountNumber,
                category,
                transactionType,
                startDate.atStartOfDay(),
                endDate.atTime(23, 59, 59, 999999));

        HttpReport httpReport = HttpReport.createReport(fileExtension);
        httpReport.downloadTransactionDetailsReport(finTransactionsDetails, response);
    }
}