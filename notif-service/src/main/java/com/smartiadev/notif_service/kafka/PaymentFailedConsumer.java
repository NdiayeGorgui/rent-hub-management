package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentFailedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentFailedConsumer {

    private final NotificationRepository repository;

    @KafkaListener(
            topics = "payment.failed",
            groupId = "notification-group"
    )
    public void onPaymentFailed(PaymentFailedEvent event) {

        repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "❌ Paiement échoué : " + event.reason(),
                        "PAYMENT_FAILED",
                        false,
                        LocalDateTime.now()
                )
        );

        System.out.println(
                "⚠️ Notification paiement échoué sauvegardée : "
                        + event.userId()
        );
    }
}
