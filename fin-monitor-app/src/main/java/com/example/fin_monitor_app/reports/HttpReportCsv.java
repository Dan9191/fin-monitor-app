package com.example.fin_monitor_app.reports;

import com.example.fin_monitor_app.entity.FinTransaction;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HttpReportCsv extends HttpReport {
    public HttpReportCsv() {
        super(".csv");
    }

    @Override
    public void downloadTransactionDetailsReport(List<FinTransaction> transactions, HttpServletResponse response) throws IOException {
        response.reset();
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
}