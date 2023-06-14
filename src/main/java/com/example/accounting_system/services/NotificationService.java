package com.example.accounting_system.services;

import com.example.accounting_system.dtos.NotificationDto;
import com.example.accounting_system.entities.Debt;
import com.example.accounting_system.repositories.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final DebtRepository debtRepository;

    /**
     * Creates notifications for all debts.
     *
     * @param notificationDto the DTO containing notification information
     * @return a list of updated debts
     */
    public List<Debt> createNotifications(NotificationDto notificationDto) {
        List<Debt> debts = debtRepository.findAll();
        debts.forEach(debt -> {
            if (!debt.isDebtStatus()) {
                debt.setNotified(notificationDto.isNotified());
                debt.setEmail(notificationDto.getEmail());
            }
        });
        return debtRepository.saveAll(debts);
    }

    /**
     * Enables notification for a specific debt.
     *
     * @param debtId the ID of the debt
     * @return the updated debt with notification enabled
     * @throws RuntimeException if the debt is not found
     */
    public Debt enableNotification(Long debtId, NotificationDto notificationDto) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new RuntimeException("Debt not found with ID: " + debtId));
        if (!debt.isDebtStatus()) {
            debt.setNotified(notificationDto.isNotified());
            debt.setEmail(notificationDto.getEmail());
        } else {
            throw new RuntimeException("You can't enable notification because you already payed your debt");
        }
        return debtRepository.save(debt);
    }
}
