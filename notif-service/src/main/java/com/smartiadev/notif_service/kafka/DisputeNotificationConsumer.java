package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.DisputeCreatedEvent;
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
public class DisputeNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    private static final UUID ADMIN_ID =
            UUID.fromString("5b2c28b5-a71a-4699-bd8e-36871037c778");

    @KafkaListener(topics = "dispute.created", groupId = "notification-group")
    public void onDisputeCreated(DisputeCreatedEvent event) {

        Notification notification = repository.save(
                new Notification(
                        null,
                        ADMIN_ID,
                        "⚠️ Nouvelle dispute signalée (raison: " + event.reason() + ")",
                        "DISPUTE",
                        false,
                        LocalDateTime.now()
                )
        );

        // 📡 envoyer la notification au frontend admin
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + ADMIN_ID,
                notification
        );

        System.out.println("⚠️ Notification dispute envoyée + websocket");
    }
}