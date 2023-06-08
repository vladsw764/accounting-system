package com.example.accounting_system.dtos;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TransactionDto {
    private Date date;
    private String category;
    private BigDecimal amount;
    private String comment;
}
