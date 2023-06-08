package com.example.accounting_system.services;

import com.example.accounting_system.dtos.TransactionDto;
import com.example.accounting_system.entities.Transaction;
import com.example.accounting_system.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public Transaction addTransaction(TransactionDto transactionDto) {
        return transactionRepository.save(convertToTransaction(transactionDto));
    }

    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto) {
        Transaction existTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        existTransaction.setAmount(transactionDto.getAmount());
        existTransaction.setCategory(transactionDto.getCategory());
        existTransaction.setDate(transactionDto.getDate());
        existTransaction.setComment(transactionDto.getComment());
        return transactionRepository.save(existTransaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public void removeTransactionFromHistory(Long transactionId) {
        Transaction existTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        transactionRepository.deleteById(transactionId);
    }

    private Transaction convertToTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setDate(transactionDto.getDate());
        transaction.setCategory(transactionDto.getCategory());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setComment(transactionDto.getComment());
        return transaction;
    }
}
