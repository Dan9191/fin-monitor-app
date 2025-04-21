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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.fin_monitor_app.model.TransactionTypeEnum.INCOME;
import static com.example.fin_monitor_app.model.TransactionTypeEnum.OUTCOME;

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

        List<LocalDate> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            last7Days.add(LocalDate.now().minusDays(i));
        }

        // Транзакции с доходами
        List<BigDecimal> incomeTransactionsSum = last7Days.stream()
                .map(date -> finTransactions.stream()
                        .filter(t -> t.getCreateDate().toLocalDate().equals(date))
                        .filter(t -> t.getTransactionType().getId().equals(INCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .toList();
        BigDecimal incomeSum = incomeTransactionsSum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Транзакции с расходами
        List<BigDecimal> outcomeTransactionsSum = last7Days.stream()
                .map(date -> finTransactions.stream()
                        .filter(t -> t.getCreateDate().toLocalDate().equals(date))
                        .filter(t -> t.getTransactionType().getId().equals(OUTCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .toList();
        BigDecimal expensesSum = outcomeTransactionsSum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM");
        List<String> formattedDates = last7Days.stream()
                .map(date -> date.format(formatter))
                .collect(Collectors.toList());

        // Группировка по категориям для операций последней недели
        Map<String, Long> transactionsByCategory = finTransactions.stream()
                .filter(t -> last7Days.contains(t.getCreateDate().toLocalDate()))
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.counting()
                ));

        model.addAttribute("user", user);
        model.addAttribute("bankAccounts", accounts);
        model.addAttribute("finTransactions", finTransactions);
        model.addAttribute("createBankAccountDto", new CreateBankAccountDto());
        model.addAttribute("createFinTransactionDto", new CreateFinTransactionDto());
        model.addAttribute("chartDates", formattedDates);
        model.addAttribute("outcomeTransactionsSum", outcomeTransactionsSum);
        model.addAttribute("incomeTransactionsSum", incomeTransactionsSum);
        model.addAttribute("transactionsByCategory", transactionsByCategory);
        model.addAttribute("expensesSum", expensesSum);
        model.addAttribute("incomeSum", incomeSum);
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
        Map<String, Long> transactionsByCategory = transactions.stream()
                .filter(t -> last30Days.contains(t.getCreateDate().toLocalDate()))
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.counting()
                ));

        model.addAttribute("finTransactions", transactions);
        model.addAttribute("transactionsByCategory", transactionsByCategory);
        return "account/fin-operations";
    }

}
