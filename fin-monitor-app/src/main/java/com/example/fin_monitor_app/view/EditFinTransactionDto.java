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
public class EditFinTransactionDto extends CreateFinTransactionDto {

    private int id;

}
