package com.smartiadev.payments_service.dto;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Double amount,
        String status,
        LocalDateTime createdAt
) {}

