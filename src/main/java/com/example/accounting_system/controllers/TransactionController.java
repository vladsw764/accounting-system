package com.example.accounting_system.controllers;

import com.example.accounting_system.dtos.TransactionDto;
import com.example.accounting_system.entities.Transaction;
import com.example.accounting_system.services.DebtService;
import com.example.accounting_system.services.PaymentService;
import com.example.accounting_system.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TransactionController {

    private final TransactionService transactionService;
    private final PaymentService paymentService;
    private final DebtService debtService;

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getCurrentBalance() {
        BigDecimal transactionBalance = transactionService.getCurrentBalance();
        BigDecimal paymentBalance = paymentService.getCurrentBalance();
        BigDecimal debtBalance = debtService.getCurrentBalance();

        BigDecimal totalBalance = transactionBalance.add(paymentBalance).add(debtBalance);

        return ResponseEntity.ok(totalBalance);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@RequestBody TransactionDto transactionDto) {
        Transaction createdTransaction = transactionService.addTransaction(transactionDto);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransactionData(@PathVariable("id") Long transactionId,
                                                             @RequestBody TransactionDto transactionDto) {
        Transaction updatedTransaction = transactionService.updateTransaction(transactionId, transactionDto);
        return ResponseEntity.ok(updatedTransaction);
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> removeTransactionFromHistory(@PathVariable("id") Long transactionId) {
        transactionService.removeTransactionFromHistory(transactionId);
        return ResponseEntity.ok("Transaction successfully removed from history!");
    }
}
