package com.example.fin_monitor_app.view;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ReportFilterDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private List<Integer> statusIds;
    private List<Integer> categoryIds;
    private List<Integer> bankAccountIds;
    private List<Integer> transactionTypeIds;
    private String fileExtension;

    public boolean hasFilters() {
        return dateFrom != null || dateTo != null || amountFrom != null || amountTo != null;
    }

}
