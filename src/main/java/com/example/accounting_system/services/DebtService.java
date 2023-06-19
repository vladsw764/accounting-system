package com.example.accounting_system.services;

import com.example.accounting_system.dtos.DebtDto;
import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.repositories.DebtRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class DebtService {
    private final DebtRepository debtRepository;
    private final JavaMailSender javaMailSender;

    /**
     * Adds a new debt.
     *
     * @param debtDto the DTO containing debt information
     * @return the created debt
     */
    public Debt addDebt(DebtDto debtDto) {
        // Convert DebtDto to Debt entity
        Debt debt = convertToDebt(debtDto);
        // Save the debt
        return debtRepository.save(debt);
    }

    /**
     * Retrieves the current debt balance.
     *
     * @return the current debt balance
     */
    public BigDecimal getCurrentBalance() {
        List<Debt> debts = debtRepository.findAll();
        BigDecimal balance = BigDecimal.ZERO;

        for (Debt debt : debts) {
            balance = balance.add(debt.getReceivedAmount());
        }

        return balance;
    }

    /**
     * Updates a debt.
     *
     * @param debtId  the ID of the debt to update
     * @param debtDto the updated debt DTO
     * @return the updated debt
     * @throws RuntimeException if the debt is not found
     */
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

    /**
     * Retrieves a debt by ID.
     *
     * @param debtId the ID of the debt
     * @return the debt
     * @throws RuntimeException if the debt is not found
     */
    public Debt getDebtById(Long debtId) {
        return debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));
    }

    /**
     * Retrieves all debts.
     *
     * @return a list of all debts
     */
    public List<Debt> getAllDebts() {
        return debtRepository.findAll();
    }

    /**
     * Scheduled task that sends debt reminders by email every 10 minutes.
     */
    @Scheduled(cron = "0 0 9 * * *") // Run every 10 minutes
    public void sendDebtReminders() {
        List<Debt> debts = debtRepository.findAll();
        Date currentDate = new Date();

        for (Debt debt : debts) {
            if (debt.isNotified() && !debt.isDebtStatus()) {
                List<Date> paymentPlan = generatePaymentPlan(debt.getId());
                for (Date paymentDate : paymentPlan) {
                    if (isToday(paymentDate, currentDate)) {
                        log.info("Sending payment reminder for debt ID: " + debt.getId());
                        sendReminderEmail(debt);
                        break; // No need to check other payment dates for the same debt
                    }
                }
            }
        }
    }

    /**
     * Generates a payment plan for a debt based on the periodic payment.
     *
     * @param debtId the ID of the debt
     * @return a list of payment dates
     */
    public List<Date> generatePaymentPlan(Long debtId) {
        Debt debt = getDebtById(debtId);
        Date startDate = debt.getStartDate();
        Date endDate = debt.getEndDate();
        double periodicPayment = debt.getPeriodicPayment();

        long daysBetween = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());

        int numPayments = (int) Math.ceil(daysBetween / periodicPayment);

        List<Date> paymentPlan = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        for (int i = 0; i < numPayments - 1; i++) {
            paymentPlan.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, (int) periodicPayment);
        }

        paymentPlan.add(endDate);

        return paymentPlan;
    }

    /**
     * Checks if a given date is today.
     *
     * @param date1 the first date to compare
     * @param date2 the second date to compare
     * @return true if the dates are the same day, false otherwise
     */
    private boolean isToday(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate localDate2 = date2.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate1.isEqual(localDate2);
    }


    /**
     * Sends a reminder email for a debt.
     *
     * @param debt the debt to send the reminder for
     */
    private void sendReminderEmail(Debt debt) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(debt.getEmail()); // Assuming the email address is stored in the 'email' field
        mailMessage.setSubject("Payment Reminder: " + debt.getCategory());

        // Check if it's the last day of payment
        boolean isLastDay = isLastDayOfPayment(debt);

        // Calculate the payment amount based on the periodic payment
        BigDecimal paymentAmount = isLastDay ? debt.getReturnAmount() : debt.getReturnAmount().divide(BigDecimal.valueOf(debt.getPeriodicPayment()),  RoundingMode.HALF_UP);

        mailMessage.setText("Dear recipient,\n\nThis is a reminder that your payment for the debt in the category '" +
                debt.getCategory() + "' is due soon. Please make the necessary arrangements to settle the debt.\n\n" +
                "Payment amount" + (isLastDay ? " (final installment)" : "") + ": $" + paymentAmount +
                "\n\nYour reminder: " + debt.getReminder() +
                "\nThank you.\n\nBest regards,\nYour Accounting System");

        javaMailSender.send(mailMessage);
    }

    /**
     * Checks if the current date is the last day of payment for a debt.
     *
     * @param debt the debt to check
     * @return true if it's the last day of payment, false otherwise
     */
    private boolean isLastDayOfPayment(Debt debt) {
        Date currentDate = new Date();
        long daysRemaining = ChronoUnit.DAYS.between(currentDate.toInstant(), debt.getEndDate().toInstant());
        return daysRemaining <= 0;
    }

    /**
     * Converts a DebtDto object to a Debt entity.
     *
     * @param debtDto the DebtDto object to convert
     * @return the converted Debt entity
     */
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
}
