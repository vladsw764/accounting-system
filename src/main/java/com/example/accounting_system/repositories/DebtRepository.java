package com.example.accounting_system.repositories;

import com.example.accounting_system.entities.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtRepository extends JpaRepository<Debt, Long> {
}
