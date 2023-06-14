package com.example.accounting_system.services;

import com.example.accounting_system.dtos.TransactionDto;
import com.example.accounting_system.entities.Transaction;
import com.example.accounting_system.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    @Value("${server.url}")
    private String url;

    public Transaction addTransaction(TransactionDto transactionDto) {
        Transaction transaction = convertToTransaction(transactionDto);
        log.info("check transaction amount");
        if (transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            checkTransactionAmount(transactionDto.getAmount());
            transaction.setCategory("outcome");
        } else {
            transaction.setCategory("income");
        }
        transactionDto.setAmount(transactionDto.getAmount());
        return transactionRepository.save(transaction);
    }

    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto) {
        Transaction existTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        if (transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            checkTransactionAmount(transactionDto.getAmount());
            existTransaction.setCategory("outcome");
        } else {
            existTransaction.setCategory("income");
        }
        transactionDto.setAmount(transactionDto.getAmount());

        existTransaction.setDate(transactionDto.getDate());
        existTransaction.setComment(transactionDto.getComment());
        return transactionRepository.save(existTransaction);
    }

    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public BigDecimal getCurrentBalance() {
        List<Transaction> transactions = transactionRepository.findAll();
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            balance = balance.add(transaction.getAmount());
        }

        return balance;
    }

    public void removeTransactionFromHistory(Long transactionId) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        transactionRepository.deleteById(transactionId);
    }

    private Transaction convertToTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setDate(transactionDto.getDate());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setComment(transactionDto.getComment());
        return transaction;
    }

    // Get total balance from GetMapping(
    public BigDecimal getTotalBalance() {
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(url + "/api/v1/transaction/balance", BigDecimal.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to retrieve total balance: " + response.getStatusCode());
        }
    }

    // Check if transaction amount greater than total balance
    public void checkTransactionAmount(BigDecimal transactionAmount) {
        BigDecimal totalBalance = getTotalBalance();

        // multiply transaction amount by 1, if transaction amount less than 0
        if (transactionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            transactionAmount = transactionAmount.multiply(BigDecimal.valueOf(-1));
        }

        if (totalBalance.compareTo(BigDecimal.ZERO) == 0 || transactionAmount.compareTo(totalBalance) > 0) {
            throw new RuntimeException("Invalid transaction amount");
        }
    }
}
