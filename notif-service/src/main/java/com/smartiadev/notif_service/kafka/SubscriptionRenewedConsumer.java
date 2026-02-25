package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.SubscriptionRenewedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionRenewedConsumer {

    private final NotificationRepository repository;

    @KafkaListener(
            topics = "subscription.renewed",
            groupId = "notification-group"
    )
    public void onSubscriptionRenewed(SubscriptionRenewedEvent event) {

        repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "🔄 Abonnement renouvelé jusqu’au "
                                + event.newEndDate(),
                        "SUBSCRIPTION_RENEWED",
                        false,
                        LocalDateTime.now()
                )
        );

        System.out.println(
                "🔄 Notification renouvellement sauvegardée : "
                        + event.userId()
        );
    }
}
