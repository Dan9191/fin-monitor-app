package com.example.fin_monitor_app.controller.user;

import com.example.fin_monitor_app.entity.FinTransaction;
//import com.example.fin_monitor_app.entity.Report;
import com.example.fin_monitor_app.entity.User;
import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.repository.FinTransactionRepository;
import com.example.fin_monitor_app.repository.ReportRepository;
import com.example.fin_monitor_app.service.BankAccountService;
import com.example.fin_monitor_app.service.FinTransactionService;
import com.example.fin_monitor_app.service.ReportService;
import com.example.fin_monitor_app.service.UserService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/account/reports")
@RequiredArgsConstructor
public class ReportController {
    private final UserService userService;
    private final BankAccountService bankAccountService;
    private final FinTransactionService finTransactionService;
    private final FinTransactionRepository finTransactionRepository;
    private final ReportService reportService;

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
            @RequestParam String reportFormat,
            HttpServletResponse response) throws IOException {

        User user = userService.findByLogin(principal.getName());
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59, 999999) : null;

        System.out.println(transactionType);

        List<FinTransaction> transactions = reportService.getFilteredTransactions(
                user,
                bankAccountNumber,
                category,
                transactionType,
                startDateTime,
                endDateTime
        );

        System.out.println("Размер списка transactions: " + transactions.size());

        if (reportFormat.equals("Excel")) {
            String fileName = "financial_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

            // Устанавливаем заголовки ответа
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
            response.setHeader(HttpHeaders.EXPIRES, "0");

            try (Workbook workbook = generateExcelReport(transactions);
                 ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
                System.out.println();
            }
        } else if (reportFormat.equals("CSV")) {
            generateCsvReport(transactions, response);
        }
    }

    private void generateCsvReport(List<FinTransaction> transactions, HttpServletResponse response) throws IOException {
        String fileName = "financial_report_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
        response.setContentType("text/csv");
        response.setCharacterEncoding("UTF-8");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

        PrintWriter writer = response.getWriter();
        writer.write("Дата;Категория;Тип;Сумма;Описание;Кошелек\n");

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (FinTransaction transaction : transactions) {
            writer.write(String.format("\"%s\";\"%s\";\"%s\";%.2f;\"%s\";\"%s\"\n",
                    transaction.getCreateDate().format(dateFormatter),
                    transaction.getCategory().getName(),
                    transaction.getTransactionType().getName(),
                    transaction.getSum().doubleValue(),
                    transaction.getCommentary() != null ? transaction.getCommentary() : "",
                    transaction.getBankAccount().getAccountNumber()));
        }
    }

    private Workbook generateExcelReport(List<FinTransaction> transactions) {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Financial Report");

        // Стиль для заголовков
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // Заголовки
        String[] headers = {"Дата", "Категория", "Тип", "Сумма", "Описание", "Кошелек"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Заполняем данными
        int rowNum = 1;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        for (FinTransaction transaction : transactions) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(transaction.getCreateDate().format(dateFormatter));
            row.createCell(1).setCellValue(transaction.getCategory().getName());
            row.createCell(2).setCellValue(transaction.getTransactionType().getName());
            row.createCell(3).setCellValue(transaction.getSum().doubleValue());
            row.createCell(4).setCellValue(transaction.getCommentary() != null ? transaction.getCommentary() : "");
            row.createCell(5).setCellValue(transaction.getBankAccount().getAccountNumber());
        }

        // Автоподбор ширины столбцов
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        return workbook;
    }
}