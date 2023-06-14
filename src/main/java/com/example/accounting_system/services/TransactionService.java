package com.example.accounting_system.services;

import com.example.accounting_system.dtos.TransactionDto;
import com.example.accounting_system.entities.Balance;
import com.example.accounting_system.entities.Transaction;
import com.example.accounting_system.repositories.BalanceRepository;
import com.example.accounting_system.repositories.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final BalanceRepository balanceRepository;
    private final RestTemplate restTemplate;
    @Value("${server.url}")
    private String url;

    /**
     * Adds a new transaction.
     *
     * @param transactionDto the transaction DTO
     * @return the created transaction
     */
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

    /**
     * Updates an existing transaction.
     *
     * @param transactionId  the ID of the transaction to update
     * @param transactionDto the updated transaction DTO
     * @return the updated transaction
     * @throws RuntimeException if the transaction is not found
     */
    public Transaction updateTransaction(Long transactionId, TransactionDto transactionDto) {
        Transaction existTransaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        if (transactionDto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            checkTransactionAmount(transactionDto.getAmount());
            existTransaction.setCategory("outcome");
        } else {
            existTransaction.setCategory("income");
        }
        existTransaction.setAmount(transactionDto.getAmount());

        existTransaction.setDate(transactionDto.getDate());
        existTransaction.setComment(transactionDto.getComment());
        return transactionRepository.save(existTransaction);
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param transactionId the ID of the transaction to retrieve
     * @return the retrieved transaction
     * @throws RuntimeException if the transaction is not found
     */
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));
    }

    /**
     * Retrieves all transactions.
     *
     * @return a list of all transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * Retrieves the current balance by summing all transaction amounts.
     *
     * @return the current balance
     */
    public BigDecimal getCurrentBalance() {
        List<Transaction> transactions = transactionRepository.findAll();
        BigDecimal balance = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            balance = balance.add(transaction.getAmount());
        }

        return balance;
    }

    /**
     * Removes a transaction from the transaction history.
     *
     * @param transactionId the ID of the transaction to remove
     * @throws RuntimeException if the transaction is not found
     */
    public void removeTransactionFromHistory(Long transactionId) {
        transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with ID: " + transactionId));

        transactionRepository.deleteById(transactionId);
    }

    /**
     * Converts a transaction DTO to a Transaction entity.
     *
     * @param transactionDto the transaction DTO to convert
     * @return the converted Transaction entity
     */
    private Transaction convertToTransaction(TransactionDto transactionDto) {
        Transaction transaction = new Transaction();
        transaction.setDate(transactionDto.getDate());
        transaction.setAmount(transactionDto.getAmount());
        transaction.setComment(transactionDto.getComment());
        return transaction;
    }

    /**
     * Retrieves the total balance from an external API.
     *
     * @return the total balance
     * @throws RuntimeException if the API call fails
     */
    public BigDecimal getTotalBalance() {
        ResponseEntity<BigDecimal> response = restTemplate.getForEntity(url + "/api/v1/transaction/balance", BigDecimal.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            throw new RuntimeException("Failed to retrieve total balance: " + response.getStatusCode());
        }
    }

    /**
     * Retrieves the balance statistics.
     *
     * @return a list of balance statistics
     */
    public List<Balance> getBalanceStatistics() {
        return balanceRepository.findAll();
    }

    /**
     * Scheduled task that collects and saves the daily balance.
     */
    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    public void collectDailyBalance() {
        BigDecimal currentBalance = getTotalBalance();
        createBalance(currentBalance);
    }

    /**
     * Creates a new balance entry with the specified amount.
     *
     * @param amount the amount to set for the balance
     */
    private void createBalance(BigDecimal amount) {
        LocalDate currentDate = LocalDate.now();
        Balance balance = new Balance();
        balance.setDate(currentDate);
        balance.setBalanceAmount(amount);

        balanceRepository.save(balance);
    }

    /**
     * Checks if the transaction amount is greater than the total balance.
     * If the transaction amount is less than or equal to zero, it is multiplied by -1.
     * Throws a RuntimeException if the transaction amount is greater than the total balance or the total balance is zero.
     *
     * @param transactionAmount The amount of the transaction to be checked
     * @throws RuntimeException If the transaction amount is invalid
     */
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
