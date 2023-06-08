package com.example.accounting_system.services;

import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.entities.Transaction;
import com.example.accounting_system.repositories.DebtRepository;
import com.example.accounting_system.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final DebtService debtService;
    private final PaymentService paymentService;
    private final DebtRepository debtRepository;

    public List<Object> getTransactionsAndDebtsByCategory(String category) {
        List<Transaction> transactions = transactionRepository.findAllByCategory(category);
        List<Debt> debts = debtRepository.findAllByCategory(category);

        if (transactions.isEmpty() && debts.isEmpty()) {
            throw new RuntimeException("No data found for category: " + category);
        } else if (!transactions.isEmpty() && !debts.isEmpty()) {
            throw new RuntimeException("Invalid category: " + category);
        } else if (!transactions.isEmpty()) {
            return new ArrayList<>(transactions);
        } else {
            return new ArrayList<>(debts);
        }
    }

    public BigDecimal getCurrentBalance() {
        BigDecimal transactionBalance = transactionService.getCurrentBalance();
        BigDecimal paymentBalance = paymentService.getCurrentBalance();
        BigDecimal debtBalance = debtService.getCurrentBalance();

        return transactionBalance.add(paymentBalance).add(debtBalance);
    }

}
