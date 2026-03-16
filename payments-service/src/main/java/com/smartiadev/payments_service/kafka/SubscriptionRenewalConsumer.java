package com.smartiadev.payments_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionRenewalRequestedEvent;
import com.smartiadev.payments_service.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionRenewalConsumer {

    private final PaymentService service;

    @KafkaListener(
            topics = "subscription.renewal.requested",
            groupId = "payment-group"
    )
    public void onRenewalRequested(SubscriptionRenewalRequestedEvent event) {

        service.createRenewalPayment(event.userId());

    }
}