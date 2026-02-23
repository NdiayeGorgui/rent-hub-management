package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.ReviewCreatedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReviewNotificationConsumer {

    private final NotificationRepository repository;

    @KafkaListener(topics = "review.created")
    public void onReviewCreated(ReviewCreatedEvent event) {

        repository.save(new Notification(
                null,
                event.reviewedUserId(),
                "⭐ Vous avez reçu un nouvel avis (" + event.rating() + "★)",
                "REVIEW",
                false,
                LocalDateTime.now()
        ));

        System.out.println(
                "⭐ Notification avis enregistrée pour l'utilisateur " +
                        event.reviewedUserId()
        );
    }
}
