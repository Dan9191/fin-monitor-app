package com.example.fin_monitor_app.view;

import com.example.fin_monitor_app.model.CategoryEnum;
import com.example.fin_monitor_app.model.OperationStatusEnum;
import com.example.fin_monitor_app.model.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateFinTransactionDto {

    private String bankAccountName;
    private CategoryEnum categoryEnum;
    private OperationStatusEnum operationStatus;
    private BigDecimal balance;
    private String commentary;
    private TransactionTypeEnum transactionType;

}
