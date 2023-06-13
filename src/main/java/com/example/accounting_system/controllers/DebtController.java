package com.example.accounting_system.controllers;

import com.example.accounting_system.dtos.DebtDto;
import com.example.accounting_system.dtos.NotificationDto;
import com.example.accounting_system.dtos.PaymentDto;
import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.entities.Payment;
import com.example.accounting_system.services.DebtService;
import com.example.accounting_system.services.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debt")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DebtController {
    private final DebtService debtService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Debt> createDebt(@RequestBody DebtDto debtDto) {
        Debt createdDept = debtService.addDebt(debtDto);
        return new ResponseEntity<>(createdDept, HttpStatus.CREATED);
    }

    @PostMapping("/notification")
    public ResponseEntity<List<Debt>> createNotification(@RequestBody NotificationDto notificationDto) {
        return new ResponseEntity<>(debtService.createNotification(notificationDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Debt> updateDebt(@PathVariable("id") Long debtId,
                                           @RequestBody DebtDto debtDto) {
        Debt updatedDebt = debtService.updateDebt(debtId, debtDto);
        return ResponseEntity.ok(updatedDebt);
    }

    @GetMapping
    public ResponseEntity<List<Debt>> getAllDebts() {
        return ResponseEntity.ok(debtService.getAllDebts());
    }

    @GetMapping("/{debtId}")
    public ResponseEntity<Payment> payDebt(@PathVariable("debtId") Long debtId,
                                           @RequestBody PaymentDto paymentDto) {
        Payment payment = paymentService.addPayment(debtId, paymentDto);
        return new ResponseEntity<>(payment, HttpStatus.CREATED);
    }

    @PutMapping("/{debtId}/{paymentId}")
    public ResponseEntity<Payment> updatePaymentInformation(@PathVariable("debtId") Long debtId,
                                                            @PathVariable("paymentId") Long paymentId,
                                                            @RequestBody PaymentDto paymentDto) {
        Payment updatedPayment = paymentService.updatePayment(debtId, paymentId, paymentDto);
        return ResponseEntity.ok(updatedPayment);
    }

}
