package com.example.fin_monitor_app.view;

import com.example.fin_monitor_app.model.PersonTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateBankAccountDto {

    private String bankAccountName;
    private PersonTypeEnum personType;
    private BigDecimal balance;

}
