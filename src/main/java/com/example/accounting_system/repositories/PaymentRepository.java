package com.example.accounting_system.repositories;

import com.example.accounting_system.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findAllByDebtId(Long id);
}
