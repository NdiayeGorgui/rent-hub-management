package com.smartiadev.base_domain_service.dto;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentCompletedEvent(
        Long paymentId,
        UUID userId,
        Double amount,
        LocalDateTime paidAt
) {}
