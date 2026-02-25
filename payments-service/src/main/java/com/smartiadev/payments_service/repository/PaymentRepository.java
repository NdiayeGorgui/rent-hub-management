package com.smartiadev.payments_service.repository;

import com.smartiadev.payments_service.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(UUID userId);
}

