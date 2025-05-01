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
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.LinkedHashMap;
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
                            @RequestParam(defaultValue = "week") String period,
                            @ModelAttribute TransactionFilterDto filter) {
        User user = userService.findByLogin(principal.getName());
        List<BankAccount> accounts = bankAccountService.getBankAccounts(user);

        // Определяем период
        LocalDateTime startDate;
        int daysToShow;
        DateTimeFormatter formatter;
        String periodLabel;

        switch (period) {
            case "month":
                startDate = LocalDateTime.now().minusMonths(1);
                daysToShow = 30;
                formatter = DateTimeFormatter.ofPattern("dd.MM");
                periodLabel = "за 30 дней";
                break;
            case "quarter":
                startDate = LocalDateTime.now().minusMonths(3);
                daysToShow = 12; // 12 недель
                formatter = DateTimeFormatter.ofPattern("ww (MMM)");
                periodLabel = "за квартал";
                break;
            case "year":
                startDate = LocalDateTime.now().minusYears(1);
                daysToShow = 12; // 12 месяцев
                formatter = DateTimeFormatter.ofPattern("MMM yyyy");
                periodLabel = "за год";
                break;
            case "week":
            default:
                startDate = LocalDateTime.now().minusDays(7);
                daysToShow = 7;
                formatter = DateTimeFormatter.ofPattern("dd.MM");
                periodLabel = "за 7 дней";
        }

        List<FinTransaction> periodTransactions =
                finTransactionService.getFinTransactionsByPeriod(user, startDate, LocalDateTime.now());

        Page<FinTransaction> transactionsPage = finTransactionService.getFilteredTransactions(
                accounts.stream().map(BankAccount::getId).toList(),
                filter,
                page,
                5
        );

        // Генерация дат/периодов для графика
        List<String> formattedDates = new ArrayList<>();
        List<LocalDate> datePoints = new ArrayList<>();

        if (period.equals("year")) {
            // Для года группируем по месяцам
            for (int i = 11; i >= 0; i--) {
                LocalDate month = LocalDate.now().minusMonths(i);
                datePoints.add(month.withDayOfMonth(1));
                formattedDates.add(month.format(formatter));
            }
        } else if (period.equals("quarter")) {
            // Для квартала группируем по неделям
            for (int i = 11; i >= 0; i--) {
                LocalDate week = LocalDate.now().minusWeeks(i);
                datePoints.add(week);
                formattedDates.add(week.format(formatter));
            }
        } else {
            // Для месяца и недели - по дням
            for (int i = daysToShow-1; i >= 0; i--) {
                LocalDate day = LocalDate.now().minusDays(i);
                datePoints.add(day);
                formattedDates.add(day.format(formatter));
            }
        }

        // Подсчет сумм по периодам
        List<BigDecimal> incomeTransactionsSum = new ArrayList<>();
        List<BigDecimal> outcomeTransactionsSum = new ArrayList<>();

        for (LocalDate datePoint : datePoints) {
            if (period.equals("year")) {
                // Группировка по месяцам
                incomeTransactionsSum.add(periodTransactions.stream()
                        .filter(t -> t.getCreateDate().getMonth() == datePoint.getMonth() &&
                                t.getCreateDate().getYear() == datePoint.getYear())
                        .filter(t -> t.getTransactionType().getId().equals(INCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

                outcomeTransactionsSum.add(periodTransactions.stream()
                        .filter(t -> t.getCreateDate().getMonth() == datePoint.getMonth() &&
                                t.getCreateDate().getYear() == datePoint.getYear())
                        .filter(t -> t.getTransactionType().getId().equals(OUTCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (period.equals("quarter")) {
                // Группировка по неделям
                int week = datePoint.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
                int year = datePoint.getYear();

                incomeTransactionsSum.add(periodTransactions.stream()
                        .filter(t -> t.getCreateDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == week &&
                                t.getCreateDate().getYear() == year)
                        .filter(t -> t.getTransactionType().getId().equals(INCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

                outcomeTransactionsSum.add(periodTransactions.stream()
                        .filter(t -> t.getCreateDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) == week &&
                                t.getCreateDate().getYear() == year)
                        .filter(t -> t.getTransactionType().getId().equals(OUTCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            } else {
                // Группировка по дням
                incomeTransactionsSum.add(periodTransactions.stream()
                        .filter(t -> t.getCreateDate().toLocalDate().equals(datePoint))
                        .filter(t -> t.getTransactionType().getId().equals(INCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

                outcomeTransactionsSum.add(periodTransactions.stream()
                        .filter(t -> t.getCreateDate().toLocalDate().equals(datePoint))
                        .filter(t -> t.getTransactionType().getId().equals(OUTCOME.getId()))
                        .map(FinTransaction::getSum)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            }
        }

        BigDecimal expensesSum = periodTransactions.stream()
                .filter(t -> t.getTransactionType().getId().equals(OUTCOME.getId()))
                .map(FinTransaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal incomeSum = periodTransactions.stream()
                .filter(t -> t.getTransactionType().getId().equals(INCOME.getId()))
                .map(FinTransaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Группировка по категориям для операций за указанный срок
        Map<String, BigDecimal> transactionsByCategory = periodTransactions.stream()
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

        // Статистика по банкам отправителям
        Map<String, Long> senderBanksStats = periodTransactions.stream()
                .filter(t -> t.getSenderBank() != null && !t.getSenderBank().isEmpty())
                .collect(Collectors.groupingBy(
                        FinTransaction::getSenderBank,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Статистика по банкам получателям
        Map<String, Long> recipientBanksStats = periodTransactions.stream()
                .filter(t -> t.getRecipientBank() != null && !t.getRecipientBank().isEmpty())
                .collect(Collectors.groupingBy(
                        FinTransaction::getRecipientBank,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        // Статистика по статусам операций
        long completedCount = periodTransactions.stream()
                .filter(t -> t.getOperationStatus().getId() == OperationStatusEnum.COMPLETED.getId())
                .count();

        long deletedCount = periodTransactions.stream()
                .filter(t -> t.getOperationStatus().getId() == OperationStatusEnum.DELETED.getId())
                .count();

        // Суммарные доходы и расходы
        BigDecimal totalIncome = periodTransactions.stream()
                .filter(t -> t.getTransactionType().getId() == INCOME.getId())
                .map(FinTransaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutcome = periodTransactions.stream()
                .filter(t -> t.getTransactionType().getId() == OUTCOME.getId())
                .map(FinTransaction::getSum)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> incomeOutcomeComparison = new LinkedHashMap<>();
        incomeOutcomeComparison.put("Поступления", totalIncome);
        incomeOutcomeComparison.put("Списания", totalOutcome);
        Map<String, Long> transactionStatusStats = new LinkedHashMap<>();

        transactionStatusStats.put("Выполненные", completedCount);
        transactionStatusStats.put("Удаленные", deletedCount);
        model.addAttribute("periodLabel", periodLabel);
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
        model.addAttribute("period", period);
        model.addAttribute("senderBanksStats", senderBanksStats);
        model.addAttribute("recipientBanksStats", recipientBanksStats);
        model.addAttribute("transactionStatusStats", transactionStatusStats);
        model.addAttribute("incomeOutcomeComparison", incomeOutcomeComparison);

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
