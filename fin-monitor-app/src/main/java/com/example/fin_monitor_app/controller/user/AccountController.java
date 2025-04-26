package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.BankAccount;
import com.example.fin_monitor_app.entity.FinTransaction;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.BankAccountCreationResult;
import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.model.OperationStatusEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.UserService;
import com.example.fin_monitor_app.view.CreateBankAccountDto;
import com.example.fin_monitor_app.view.CreateFinTransactionDto;
import com.example.fin_monitor_app.view.EditFinTransactionDto;
import com.example.fin_monitor_app.view.TransactionFilterDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.example.fin_monitor_app.model.OperationStatusEnum.DELETED;
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
    public String dashboard(Principal principal,
                            Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @ModelAttribute TransactionFilterDto filter) {
        User user = userService.findByLogin(principal.getName());
        List<BankAccount> accounts = bankAccountService.getBankAccounts(user);
        List<FinTransaction> last7DaysTransactions =
                finTransactionService.getFinTransactionsByPeriod(user, LocalDateTime.now().minusDays(7), LocalDateTime.now());

        Page<FinTransaction> transactionsPage = finTransactionService.getFilteredTransactions(
                accounts.stream().map(BankAccount::getId).toList(),
                filter,
                page,
                5
        );

        List<LocalDate> last7Days = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            last7Days.add(LocalDate.now().minusDays(i));
        }

        // Транзакции с доходами
        List<BigDecimal> incomeTransactionsSum = last7Days.stream()
                .map(date -> last7DaysTransactions.stream()
                        .filter(t -> t.getCreateDate().toLocalDate().equals(date))
                        .filter(t -> t.getTransactionType().getId().equals(INCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add))
                .toList();
        BigDecimal incomeSum = incomeTransactionsSum.stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        // Транзакции с расходами
        List<BigDecimal> outcomeTransactionsSum = last7Days.stream()
                .map(date -> last7DaysTransactions.stream()
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
        Map<String, BigDecimal> transactionsByCategory = last7DaysTransactions.stream()
                .filter(t -> last7Days.contains(t.getCreateDate().toLocalDate()))
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
        model.addAttribute("user", user);
        model.addAttribute("bankAccounts", accounts);
        model.addAttribute("transactionsPage", transactionsPage);
        model.addAttribute("createBankAccountDto", new CreateBankAccountDto());
        model.addAttribute("createFinTransactionDto", new CreateFinTransactionDto());
        model.addAttribute("chartDates", formattedDates);
        model.addAttribute("outcomeTransactionsSum", outcomeTransactionsSum);
        model.addAttribute("incomeTransactionsSum", incomeTransactionsSum);
        model.addAttribute("transactionsByCategory", transactionsByCategory);
        model.addAttribute("expensesSum", expensesSum);
        model.addAttribute("incomeSum", incomeSum);
        model.addAttribute("currentUri", "/account/dashboard");
        model.addAttribute("filter", filter);
        return "account/dashboard";
    }

    @PostMapping("/create-account")
    public String createAccount(
            @ModelAttribute CreateBankAccountDto createBankAccountDto,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        User user = userService.findByLogin(principal.getName());
        BankAccountCreationResult bankAccountCreationResult = bankAccountService.save(createBankAccountDto, user);
        if (!bankAccountCreationResult.isCreated()) {
            redirectAttributes.addFlashAttribute("errorMessage", bankAccountCreationResult.errorMessage());
        }
        return "redirect:/account/dashboard";
    }

    @PostMapping("/create-fin-transaction")
    public String createFinTransaction(@ModelAttribute CreateFinTransactionDto createFinTransactionDto,  HttpServletRequest request) {

        finTransactionService.save(createFinTransactionDto);
        // Получаем URL предыдущей страницы из заголовка "Referer"
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/account/dashboard");
    }

    @PostMapping("/delete-account/{id}") // Изменили на POST
    public String deleteAccount(
            @PathVariable Integer id) {
        BankAccount account = bankAccountService.getBankAccountById(id);
        bankAccountService.delete(account);
        return "redirect:/account/dashboard";
    }

    @PostMapping("/delete-transaction/{id}")
    public String markTransactionAsDeleted(
            @PathVariable Long id,
            /* Получаем запрос (для получения страницы, так как у нас в двух местах удаление операции есть
             и нужно делать корректный переход)
             */
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        try {
            finTransactionService.markAsDeleted(id);
        } catch (NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        // Получаем URL предыдущей страницы из заголовка "Referer"
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/account/dashboard");
    }
    @GetMapping("/transaction/{id}")
    @ResponseBody
    public EditFinTransactionDto getTransactionData(@PathVariable Long id) {
        FinTransaction transaction = finTransactionService.findTransactionById(id);
        EditFinTransactionDto dto = new EditFinTransactionDto();
        dto.setId(transaction.getId());
        dto.setBankAccountName(transaction.getBankAccount().getAccountName());
        dto.setTransactionType(TransactionTypeEnum.fromId(transaction.getTransactionType().getId()));
        dto.setCategoryEnum(CategoryEnum.fromId(transaction.getCategory().getId()));
        dto.setOperationStatus(OperationStatusEnum.fromId(transaction.getOperationStatus().getId()));
        dto.setBalance(transaction.getSum());
        dto.setCommentary(transaction.getCommentary());
        dto.setWithdrawalAccount(transaction.getWithdrawalAccount());
        dto.setRecipientBankAccount(transaction.getRecipientBankAccount());
        dto.setSenderBank(transaction.getSenderBank());
        dto.setRecipientTelephoneNumber(transaction.getRecipientTelephoneNumber());
        dto.setRecipientBank(transaction.getRecipientBank());
        dto.setRecipientTin(transaction.getRecipientTin());

        return dto;
    }

    @PostMapping("/edit-fin-transaction/{id}")
    public String editFinTransaction(@PathVariable int id,
                                     @ModelAttribute EditFinTransactionDto editFinTransactionDto,
                                     HttpServletRequest request) {
        editFinTransactionDto.setId(id);
        finTransactionService.update(editFinTransactionDto);

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/account/dashboard");
    }
}
