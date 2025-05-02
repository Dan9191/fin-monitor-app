package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.reports.HttpReport;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.ReportFilterDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/account/reports")
@RequiredArgsConstructor
public class ReportController {

    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final FinTransactionService finTransactionService;

    @GetMapping
    public String reportsPage(Principal principal, Model model) {
        User user = userService.findByLogin(principal.getName());
        model.addAttribute("user", user);
        model.addAttribute("bankAccounts", bankAccountService.getBankAccounts(user));

        // Добавляем DTO с дефолтными значениями
        ReportFilterDto filterDto = new ReportFilterDto();
        filterDto.setDateFrom(LocalDate.now().withDayOfMonth(1));
        filterDto.setDateTo(LocalDate.now());
        model.addAttribute("filterDto", filterDto);

        return "account/reports";
    }

    @PostMapping("/generate-report")
    public void generateReport(
            @ModelAttribute ReportFilterDto filterDto,
            @RequestParam(required = false, defaultValue = "false") boolean allAccountsSelected,
            Principal principal,
            HttpServletResponse response) throws IOException {

        User user = userService.findByLogin(principal.getName());

        // Если выбрано "Все счета" или не выбрано ни одного
        if(allAccountsSelected || filterDto.getBankAccountIds() == null || filterDto.getBankAccountIds().isEmpty()) {
            List<BankAccount> allAccounts = bankAccountService.getBankAccounts(user);
            filterDto.setBankAccountIds(allAccounts.stream()
                    .map(BankAccount::getId)
                    .collect(Collectors.toList()));
        }

        List<FinTransaction> transactions = finTransactionService.getFilteredTransactions(filterDto);
        HttpReport httpReport = HttpReport.createReport(filterDto.getFileExtension());
        httpReport.downloadTransactionDetailsReport(transactions, response);
    }
}