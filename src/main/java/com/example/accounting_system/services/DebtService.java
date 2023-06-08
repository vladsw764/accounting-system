package com.example.accounting_system.services;

import com.example.accounting_system.dtos.DebtDto;
import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.repositories.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;

    // Methods for adding, updating, and retrieving debts
    public Debt addDebt(DebtDto debtDto) {
        // Convert DebtDto to Debt entity
        Debt debt = convertToDebt(debtDto);
        // Save the debt
        return debtRepository.save(debt);
    }

    public BigDecimal getCurrentBalance() {
        List<Debt> debts = debtRepository.findAll();
        BigDecimal balance = BigDecimal.ZERO;

        for (Debt debt : debts) {
            balance = balance.add(debt.getReceivedAmount()).subtract(debt.getReturnAmount());
        }

        return balance;
    }

    public Debt updateDebt(Long debtId, DebtDto debtDto) {
        // Find the debt by ID
        Debt existingDebt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));

        // Update the debt with new values
        existingDebt.setStartDate(debtDto.getStartDate());
        existingDebt.setEndDate(debtDto.getEndDate());
        existingDebt.setCategory(debtDto.getCategory());
        existingDebt.setReceivedAmount(debtDto.getReceivedAmount());
        existingDebt.setReturnAmount(debtDto.getReturnAmount());
        existingDebt.setPeriodicPayment(debtDto.getPeriodicPayment());
        existingDebt.setReminder(debtDto.getReminder());

        // Save the updated debt
        return debtRepository.save(existingDebt);
    }

    public List<Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    private Debt convertToDebt(DebtDto debtDto) {
        Debt debt = new Debt();
        debt.setStartDate(debtDto.getStartDate());
        debt.setEndDate(debtDto.getEndDate());
        debt.setCategory(debtDto.getCategory());
        debt.setReceivedAmount(debtDto.getReceivedAmount());
        debt.setReturnAmount(debtDto.getReturnAmount());
        debt.setPeriodicPayment(debtDto.getPeriodicPayment());
        debt.setReminder(debtDto.getReminder());
        return debt;
    }
}
