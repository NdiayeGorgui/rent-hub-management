package com.smartiadev.payments_service.repository;

import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(UUID userId);

    //stats
    Long countByStatus(PaymentStatus status);

    @Query("""
        SELECT SUM(p.amount)
        FROM Payment p
        WHERE p.status = 'SUCCESS'
    """)
    Double sumSuccessfulPayments();
}

