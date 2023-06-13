package com.example.accounting_system.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationDto {
    private boolean isNotified;
    private String email;
}
