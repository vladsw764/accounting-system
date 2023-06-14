package com.example.accounting_system.services;

import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.entities.Transaction;
import com.example.accounting_system.repositories.DebtRepository;
import com.example.accounting_system.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final TransactionRepository transactionRepository;

    private final DebtRepository debtRepository;

    /**
     * Retrieves all transactions and debts for a given category.
     *
     * @param category the category to retrieve data for
     * @return a list of transactions or debts for the category
     * @throws RuntimeException if no data is found or if an invalid category is provided
     */
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
}
