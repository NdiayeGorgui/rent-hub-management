package com.smartiadev.base_domain_service.dto;


import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCreatedEvent(
        Long paymentId,
        String paymentIntentId,
        UUID userId,
        String fullName,
        Double amount,
        LocalDateTime paidAt
) {}
