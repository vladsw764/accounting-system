package com.example.accounting_system.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class DebtDto {
    private Date startDate;
    private Date endDate;
    private String category;
    private BigDecimal receivedAmount;
    private BigDecimal returnAmount;
    private double periodicPayment;
    private String reminder;
}
