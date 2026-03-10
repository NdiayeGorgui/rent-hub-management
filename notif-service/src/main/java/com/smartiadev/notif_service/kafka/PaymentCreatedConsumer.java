package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentCreatedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentCreatedConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final UUID ADMIN_ID =
            UUID.fromString("5b2c28b5-a71a-4699-bd8e-36871037c778");

    @KafkaListener(
            topics = "payment.created",
            groupId = "notification-group"
    )
    public void onPaymentCreated(PaymentCreatedEvent event) {

        // notification pour l'utilisateur
        Notification userNotification = repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "✅ Paiement envoyé : " + event.amount() + "$",
                        "PAYMENT_CREATED",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 envoyer au frontend via websocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + event.userId(),
                userNotification
        );

        // notification pour l'admin
        Notification adminNotification = repository.save(
                new Notification(
                        null,
                        ADMIN_ID,
                        "💰 Nouveau paiement envoyé par "
                                + event.fullName()
                                + " : " + event.amount() + "$",
                        "ADMIN_PAYMENT_CREATED",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 websocket pour l'admin
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + ADMIN_ID,
                adminNotification
        );

        System.out.println(
                "💰 Notification paiement envoyée + websocket"
        );
    }
}