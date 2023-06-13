package com.example.accounting_system.services;

import com.example.accounting_system.dtos.DebtDto;
import com.example.accounting_system.dtos.NotificationDto;
import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.repositories.DebtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;
    private final JavaMailSender javaMailSender;

    // Methods for adding, updating, and retrieving debts
    public Debt addDebt(DebtDto debtDto) {
        // Convert DebtDto to Debt entity
        Debt debt = convertToDebt(debtDto);
        // Save the debt
        return debtRepository.save(debt);
    }

    public BigDecimal getCurrentBalance() {
        List<Debt> debts = debtRepository.findAll();
        BigDecimal balance = BigDecimal.ZERO;

        for (Debt debt : debts) {
            balance = balance.add(debt.getReceivedAmount());
        }

        return balance;
    }

    public Debt updateDebt(Long debtId, DebtDto debtDto) {
        // Find the debt by ID
        Debt existingDebt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));

        // Update the debt with new values
        existingDebt.setStartDate(debtDto.getStartDate());
        existingDebt.setEndDate(debtDto.getEndDate());
        existingDebt.setCategory(debtDto.getCategory());
        existingDebt.setReceivedAmount(debtDto.getReceivedAmount());
        existingDebt.setReturnAmount(debtDto.getReturnAmount());
        existingDebt.setPeriodicPayment(debtDto.getPeriodicPayment());
        existingDebt.setReminder(debtDto.getReminder());

        // Save the updated debt
        return debtRepository.save(existingDebt);
    }

    public List<Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    @Scheduled(cron = "0 0 9 * * *") // Run every day at 9 AM
    public void sendDebtReminders() {
        List<Debt> debts = debtRepository.findAll();
        Date currentDate = new Date();

        for (Debt debt : debts) {
            if (debt.isNotified() && (debt.getEndDate().getTime() - currentDate.getTime()) <= 7L * 24L * 60L * 60L * 1000L) {
                log.info("Try to send notification by email");
                sendReminderEmail(debt);
            }
        }
    }

    private void sendReminderEmail(Debt debt) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(debt.getEmail()); // Assuming the email address is stored in the 'reminder' field
        mailMessage.setSubject("Payment Reminder: " + debt.getCategory());
        mailMessage.setText("Dear recipient,\n\nThis is a reminder that your payment for the debt in the category '" +
                debt.getCategory() + "' is due soon. Please make the necessary arrangements to settle the debt.\n\n" +
                "Your reminder: " + debt.getReminder() +
                "\nThank you.\n\nBest regards,\nYour Accounting System");

        javaMailSender.send(mailMessage);
    }

    public List<Debt> createNotification(NotificationDto notificationDto) {
        List<Debt> debts = debtRepository.findAll();
        debts.forEach(debt -> {
            if (!debt.isDebtStatus()) {
                debt.setNotified(notificationDto.isNotified());
                debt.setEmail(notificationDto.getEmail());
            }
        });
        return debtRepository.saveAll(debts);
    }

    private Debt convertToDebt(DebtDto debtDto) {
        Debt debt = new Debt();
        debt.setStartDate(debtDto.getStartDate());
        debt.setEndDate(debtDto.getEndDate());
        debt.setCategory(debtDto.getCategory());
        debt.setReceivedAmount(debtDto.getReceivedAmount());
        debt.setReturnAmount(debtDto.getReturnAmount());
        debt.setPeriodicPayment(debtDto.getPeriodicPayment());
        debt.setReminder(debtDto.getReminder());
        debt.setNotified(false);
        return debt;
    }

    public Debt getDebtById(Long debtId) {
        return debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));
    }
}
