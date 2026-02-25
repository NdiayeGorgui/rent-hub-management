package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final NotificationRepository repository;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "notification-group"
    )
    public void onPaymentSuccess(PaymentCompletedEvent event) {

        repository.save(
                new Notification(
                        null,
                        event.userId(),
                        "✅ Paiement réussi de " + event.amount() + " $",
                        "PAYMENT_SUCCESS",
                        false,
                        LocalDateTime.now()
                )
        );

        System.out.println(
                "💳 Notification paiement réussi sauvegardée : "
                        + event.userId()
        );
    }
}
