package com.example.accounting_system.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class DebtDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;
    private String category;
    private BigDecimal receivedAmount;
    private BigDecimal returnAmount;
    private double periodicPayment;
    private boolean debtStatus; // true if the debt is closed
    private String reminder;
}
