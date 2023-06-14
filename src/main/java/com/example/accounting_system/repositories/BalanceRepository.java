package com.example.accounting_system.repositories;

import com.example.accounting_system.entities.Balance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
