package com.smartiadev.payments_service.service;

import com.smartiadev.base_domain_service.dto.PaymentCreatedEvent;
import com.smartiadev.payments_service.client.UserClient;
import com.smartiadev.payments_service.dto.CreatePaymentRequest;
import com.smartiadev.payments_service.dto.PaymentResponse;
import com.smartiadev.payments_service.dto.PaymentProviderResult;
import com.smartiadev.payments_service.entity.Payment;
import com.smartiadev.payments_service.entity.PaymentStatus;
import com.smartiadev.payments_service.kafka.PaymentEventPublisher;
import com.smartiadev.payments_service.repository.PaymentRepository;
import com.smartiadev.payments_service.stripe.PaymentProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentProvider paymentProvider;
    private final UserClient userClient;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public PaymentResponse createPayment(UUID userId, CreatePaymentRequest request) {

        PaymentProviderResult result =
                paymentProvider.charge(userId, request.amount());

        if (!result.success()) {
            paymentEventPublisher.publishPaymentFailed(userId, result.failureReason()); // 👈 EVENT
            throw new IllegalStateException(result.failureReason());
        }

        Payment payment = repository.save(
                Payment.builder()
                        .userId(userId)
                        .amount(request.amount())
                        .status(PaymentStatus.PENDING)
                        .paymentIntentId(result.transactionId())
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        String fullName = "Unknown";

        try {
            fullName = userClient.getUser(userId).fullName();
        } catch (Exception ignored) {}

        // 👇 publier event payment completed
        paymentEventPublisher.publishPaymentCreated(
                new PaymentCreatedEvent(
                        payment.getId(),
                        payment.getUserId(),
                        fullName,
                        payment.getAmount(),
                        payment.getCreatedAt()
                )
        );

        return new PaymentResponse(
                payment.getId(),
                fullName,
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getPaymentIntentId(),
                result.clientSecret()
        );
    }
    public List<PaymentResponse> getAllPayments() {

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    public List<PaymentResponse> getMyPayments(UUID userId) {

        return repository.findByUserId(userId)
                .stream()
                .map(this::map)
                .toList();
    }
    private PaymentResponse map(Payment payment) {

        String fullName = "Unknown";

        try {
            fullName = userClient.getUser(payment.getUserId()).fullName();
        } catch (Exception ignored) {}

        return new PaymentResponse(
                payment.getId(),
                fullName,
                payment.getAmount(),
                payment.getStatus().name(),
                payment.getCreatedAt(),
                payment.getPaymentIntentId(),
                null
        );
    }

    public List<PaymentResponse> getPendingPayments() {

        return repository.findByStatus(PaymentStatus.PENDING)
                .stream()
                .map(this::map)
                .toList();
    }

    @Transactional
    public PaymentResponse confirmPayment(String intentId) {

        Payment payment = repository
                .findByPaymentIntentId(intentId)
                .orElseThrow();

        payment.setStatus(PaymentStatus.SUCCESS);

        repository.save(payment);

        return map(payment);
    }
}