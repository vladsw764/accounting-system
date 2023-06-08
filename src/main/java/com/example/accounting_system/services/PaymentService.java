package com.example.accounting_system.services;

import com.example.accounting_system.dtos.PaymentDto;
import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.entities.Payment;
import com.example.accounting_system.repositories.DebtRepository;
import com.example.accounting_system.repositories.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DebtRepository debtRepository;

    // Methods for adding and updating debt payments
    public Payment addPayment(Long debtId, PaymentDto paymentDto) {
        // Find the debt by ID
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));

        // Create a new payment
        Payment payment = new Payment();
        payment.setDebt(debt);
        payment.setDate(paymentDto.getDate());
        payment.setAmount(paymentDto.getAmount());

        debt.setReturnAmount(debt.getReturnAmount().subtract(payment.getAmount()));
        debtRepository.save(debt);

        // Save the payment
        return paymentRepository.save(payment);
    }

    public Payment updatePayment(Long debtId, Long paymentId, PaymentDto paymentDto) {
        // Find the debt by ID
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));

        // Find the payment by ID
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        // Update the payment date with new values
        existingPayment.setDate(paymentDto.getDate());

        BigDecimal newAmount = (existingPayment.getAmount().compareTo(paymentDto.getAmount())) < 0
                ? paymentDto.getAmount().subtract(existingPayment.getAmount())
                : paymentDto.getAmount().add(existingPayment.getAmount());
        debt.setReturnAmount(newAmount);

        // Update the payment amount with new values
        existingPayment.setAmount(paymentDto.getAmount());

        debtRepository.save(debt);

        // Save the updated payment
        return paymentRepository.save(existingPayment);
    }

}
