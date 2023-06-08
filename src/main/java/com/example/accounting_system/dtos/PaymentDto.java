package com.example.accounting_system.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class PaymentDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private BigDecimal amount;
}
