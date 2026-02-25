package com.smartiadev.payments_service.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments", schema = "payment_schema")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // SUCCESS, FAILED, PENDING

    private LocalDateTime createdAt;
}

