package com.example.fin_monitor_app.view;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionFilterDto {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateTo;

    private BigDecimal amountFrom;
    private BigDecimal amountTo;
//    private String transactionType;
//    private String status;
//    private String category;
   //   private List<CategoryEnum> categoryEnum;

    public boolean hasFilters() {
        return dateFrom != null || dateTo != null || amountFrom != null || amountTo != null;
    }

}
