package com.example.fin_monitor_app.reports;

import com.example.fin_monitor_app.entity.FinTransaction;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpReportXlsx extends HttpReport {
    public HttpReportXlsx() {
        super("xlsx");
    }

    @Override
    public void downloadTransactionDetailsReport(List<FinTransaction> transactions, HttpServletResponse response) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Финансовый отчёт");

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

            // Заполнение данными
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

            // Настройка ответа
            response.reset();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
            response.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            response.setHeader(HttpHeaders.PRAGMA, "no-cache");
            response.setHeader(HttpHeaders.EXPIRES, "0");

            // Запись файла в выходной поток
            try (ServletOutputStream out = response.getOutputStream()) {
                workbook.write(out);
                out.flush();
            }
        }
    }
}