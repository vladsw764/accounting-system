package com.example.accounting_system.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "debts")
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date startDate;
    private Date endDate;
    private String category;
    private BigDecimal receivedAmount;
    private BigDecimal returnAmount;
    private double periodicPayment;
    private boolean debtStatus; // true if the debt is closed
    private boolean isNotified;
    private String email;
    private String reminder;
}
