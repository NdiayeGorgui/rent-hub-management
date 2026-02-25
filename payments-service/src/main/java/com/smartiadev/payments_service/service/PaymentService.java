package com.smartiadev.payments_service.service;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.payments_service.client.SubscriptionClient;
import com.smartiadev.payments_service.dto.CreatePaymentRequest;
import com.smartiadev.payments_service.dto.PaymentResponse;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.entity.PaymentStatus;
import com.smartiadev.payments_service.kafka.PaymentEventPublisher;
import com.smartiadev.payments_service.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final SubscriptionClient subscriptionClient;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse createPayment(
            UUID userId,
            CreatePaymentRequest request
    ) {

        // 🔎 VÉRIFICATION AVANT PAIEMENT
        boolean alreadyPremium = subscriptionClient.isPremium(userId);

        if (alreadyPremium) {
            throw new IllegalStateException("User is already premium");
        }

        boolean paymentOk = simulatePayment();

        if (!paymentOk) {
            eventPublisher.publishPaymentFailed(
                    userId,
                    "CARD_DECLINED"
            );
            throw new IllegalStateException("Payment failed");
        }


        Payment payment = Payment.builder()
                .userId(userId)
                .amount(request.amount())
                .status(PaymentStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(payment);

        // 🔗 SOUSCRIPTION UNE SEULE FOIS
       // subscriptionClient.subscribeInternal(userId);

        // 🔥 EVENT AU LIEU D’APPEL DIRECT
        eventPublisher.publishPaymentCompleted(
                new PaymentCompletedEvent(
                        payment.getId(),
                        userId,
                        payment.getAmount(),
                        payment.getCreatedAt()
                )
        );

        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getCreatedAt()
        );
    }


    private boolean simulatePayment() {
        return true; // Stripe plus tard
    }
}
