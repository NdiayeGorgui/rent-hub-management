package com.smartiadev.subscription_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionRenewedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishRenewed(UUID userId, LocalDateTime newEndDate) {
        kafkaTemplate.send(
                "subscription.renewed",
                new SubscriptionRenewedEvent(userId, newEndDate)
        );
    }
}
