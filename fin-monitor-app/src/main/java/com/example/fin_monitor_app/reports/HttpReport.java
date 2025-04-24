package com.example.fin_monitor_app.reports;

import com.example.fin_monitor_app.entity.FinTransaction;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public abstract class HttpReport {
    protected String fileName;

    public HttpReport(String fileExtension) {
        this.fileName = "financial_report_"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                + "." + fileExtension;
    }

    public static HttpReport createReport(String fileExtension) {
        if (fileExtension == null) {
            throw new IllegalArgumentException("File extension cannot be null");
        }

        return switch (fileExtension.toLowerCase()) {
            case "xlsx" -> new HttpReportXlsx();
            case "csv"  -> new HttpReportCsv();
            case "pdf"  -> new HttpReportPdf();
            default -> throw new IllegalArgumentException(
                    "Unsupported file extension: " + fileExtension +
                            ". Supported extensions: xlsx, csv, pdf"
            );
        };
    }

    public abstract void downloadTransactionDetailsReport(List<FinTransaction> transactions, HttpServletResponse response) throws IOException;
}