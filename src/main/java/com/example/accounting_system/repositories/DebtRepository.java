package com.example.accounting_system.repositories;

import com.example.accounting_system.entities.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findAllByCategory(String category);
}
