package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.RentalApprovedEvent;
import com.smartiadev.base_domain_service.dto.RentalCancelledEvent;
import com.smartiadev.base_domain_service.dto.RentalEndedEvent;
import com.smartiadev.base_domain_service.dto.RentalStartedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class RentalNotificationConsumer {
    private final NotificationRepository repository;

    @KafkaListener(topics = "rental.approved")
    public void approved(RentalApprovedEvent event) {
        repository.save(new Notification(
                null,
                event.renterId(),
                "Votre demande de location est approuvée ✅",
                "APPROVED",
                false,
                LocalDateTime.now()
        ));
        System.out.println(
                "📩 Notification locataire choisi : votre demande est approuvée"
        );
    }

    @KafkaListener(topics = "rental.started")
    public void started(RentalStartedEvent event) {
        repository.save(new Notification(
                null,
                event.renterId(),
                "Votre location a commencé 🚀",
                "STARTED",
                false,
                LocalDateTime.now()
        ));
        System.out.println(
                "🚀 Votre location a commencé"
        );
    }

    @KafkaListener(topics = "rental.cancelled")
    public void cancelled(RentalCancelledEvent event) {
        repository.save(new Notification(
                null,
                event.renterId(),
                "Le produit est déjà loué ❌",
                "CANCELLED",
                false,
                LocalDateTime.now()
        ));
        System.out.println(
                "❌ Désolé, le produit est déjà loué"
        );
    }

    @KafkaListener(topics = "rental.ended")
    public void ended(RentalEndedEvent event) {
        repository.save(new Notification(
                null,
                event.getRenterId(),
                "Votre location est terminée 📦",
                "ENDED",
                false,
                LocalDateTime.now()
        ));
        System.out.println(
                "❌Votre location est terminée"
        );
    }

}

