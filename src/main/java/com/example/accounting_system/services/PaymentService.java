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
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found with ID: " + paymentId));

        validatePaymentBelongsToDebt(payment, debt);
        BigDecimal previousPaymentAmount = payment.getAmount();
        updatePaymentFields(payment, paymentDto);
        BigDecimal newPaymentAmount = payment.getAmount();
        updateDebtReturnAmount(debt, previousPaymentAmount, newPaymentAmount);
        debtRepository.save(debt);
        return paymentRepository.save(payment);
    }

    private void validatePaymentBelongsToDebt(Payment payment, Debt debt) {
        if (!payment.getDebt().equals(debt)) {
            throw new IllegalArgumentException("Payment does not belong to the specified debt.");
        }
    }

    private void updatePaymentFields(Payment payment, PaymentDto paymentDto) {
        payment.setDate(paymentDto.getDate());
        payment.setAmount(paymentDto.getAmount());
    }

    private void updateDebtReturnAmount(Debt debt, BigDecimal previousPaymentAmount, BigDecimal newPaymentAmount) {
        BigDecimal returnAmountDifference = newPaymentAmount.subtract(previousPaymentAmount);
        BigDecimal currentReturnAmount = debt.getReturnAmount();
        BigDecimal newReturnAmount = currentReturnAmount.subtract(returnAmountDifference);
        debt.setReturnAmount(newReturnAmount);
    }

}
