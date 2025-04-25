package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import com.example.fin_monitor_app.view.TransactionFilterDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.fin_monitor_app.model.OperationStatusEnum.DELETED;

@Controller
@RequestMapping("/operations")
@RequiredArgsConstructor
public class OperationController {

    private final BankAccountService bankAccountService;
    private final FinTransactionService finTransactionService;
    private final UserService userService;

    @GetMapping("/{accountId}")
    public String showAccountOperations(@PathVariable Integer accountId,
                                      Model model,
                                      @RequestParam(defaultValue = "0") int page,
                                      @ModelAttribute TransactionFilterDto filter,
                                      Principal principal) {
        User user = userService.findByLogin(principal.getName());
        BankAccount account = bankAccountService.getBankAccountById(accountId);
        if (account == null) {
            return "redirect:/account/dashboard";
        }
        CreateFinTransactionDto createFinTransactionDto = new CreateFinTransactionDto();
        createFinTransactionDto.setBankAccountName(account.getAccountName());

        Page<FinTransaction> transactionsPage = finTransactionService.getFilteredTransactions(
                Collections.singletonList(account.getId()),
                filter.getDateFrom() != null ? filter.getDateFrom().atStartOfDay() : null,
                filter.getDateTo() != null ? filter.getDateTo().plusDays(1).atStartOfDay() : null,
                filter.getAmountFrom(),
                filter.getAmountTo(),
                page,
                5
        );
        List<FinTransaction> last30DaysTransactions = finTransactionService.getFinTransactionsByBankAccountAndPeriod(
                account,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        List<LocalDate> last30Days = new ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            last30Days.add(LocalDate.now().minusDays(i));
        }

        // Группировка по категориям для операций последних 30 дней
        Map<String, BigDecimal> transactionsByCategory = last30DaysTransactions.stream()
                .filter(t -> last30Days.contains(t.getCreateDate().toLocalDate()))
                .filter(t -> t.getOperationStatus().getId() != DELETED.getId())
                .filter(t -> t.getCategory() != null)
                .collect(Collectors.groupingBy(
                        t -> t.getCategory().getName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                FinTransaction::getSum,
                                BigDecimal::add
                        )
                ));


        model.addAttribute("bankAccount", account);
        model.addAttribute("transactionsPage", transactionsPage);
        model.addAttribute("transactionsByCategory", transactionsByCategory);
        model.addAttribute("bankAccounts", bankAccountService.getBankAccounts(user));
        model.addAttribute("createFinTransactionDto", createFinTransactionDto);
        model.addAttribute("currentAccountId", accountId);
        model.addAttribute("currentUri", "/operations/" + accountId);
        return "account/fin-operations";
    }

    @PostMapping("/create/{accountId}")
    public String createFinTransaction(@ModelAttribute CreateFinTransactionDto createFinTransactionDto,
                                       @PathVariable Integer accountId) {
        finTransactionService.save(createFinTransactionDto);
        return "redirect:/operations/" + accountId;
    }
}