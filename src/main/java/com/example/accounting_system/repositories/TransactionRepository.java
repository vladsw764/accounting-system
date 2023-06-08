package com.example.accounting_system.repositories;

import com.example.accounting_system.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByCategory(String category);
}
