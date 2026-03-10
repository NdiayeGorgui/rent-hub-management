package com.smartiadev.payments_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentResponse(
        Long id,
        String userFullName,
        Double amount,
        String status,
        LocalDateTime createdAt,
        String paymentIntentId,
        String clientSecret
) {}