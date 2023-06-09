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
    private final String url;

    public Transaction addTransaction(TransactionDto transactionDto) {
        log.warn("check category of transaction");
        if (transactionDto.getCategory().equals("outcome")) {
            log.warn("check transaction amount");
            checkTransactionAmount(transactionDto.getAmount());
            log.warn("multiply amount by 1");
            transactionDto.setAmount(transactionDto.getAmount().multiply(BigDecimal.valueOf(-1)));
        }
        log.info("ready to save transaction");
        return transactionRepository.save(convertToTransaction(transactionDto));
    }

    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto) {
        Transaction existTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
        if (transactionDto.getCategory().equals("outcome")) {
            checkTransactionAmount(transactionDto.getAmount());
            existTransaction.setAmount(transactionDto.getAmount().multiply(BigDecimal.valueOf(-1)));
        }
        existTransaction.setCategory(transactionDto.getCategory());
        existTransaction.setDate(transactionDto.getDate());
        existTransaction.setComment(transactionDto.getComment());
        return transactionRepository.save(existTransaction);
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
        transaction.setCategory(transactionDto.getCategory());
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

        if (totalBalance.compareTo(BigDecimal.ZERO) == 0 || transactionAmount.compareTo(totalBalance) > 0) {
            throw new RuntimeException("Invalid transaction amount");
        }
    }


}
