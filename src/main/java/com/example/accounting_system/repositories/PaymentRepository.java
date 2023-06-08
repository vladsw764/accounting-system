package com.example.accounting_system.repositories;

import com.example.accounting_system.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
