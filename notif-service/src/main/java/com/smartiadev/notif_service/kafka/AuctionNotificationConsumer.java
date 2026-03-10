package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
public class AuctionNotificationConsumer {

    private final NotificationRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(
            topics = "auction.closed",
            groupId = "notification-group"
    )
    public void onAuctionClosed(AuctionClosedEvent event) {

        Notification notification;

        if (event.winnerId() != null) {

            notification = repository.save(
                    new Notification(
                            null,
                            event.winnerId(),
                            "🎉 Félicitations ! Vous avez remporté l’enchère avec "
                                    + event.winningAmount() + " $",
                            "AUCTION_WON",
                            false,
                            LocalDateTime.now()
                    )
            );

            // 📡 envoyer au frontend
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.winnerId(),
                    notification
            );

            System.out.println("🏆 Notification envoyée + websocket");

        } else {

            notification = repository.save(
                    new Notification(
                            null,
                            event.ownerId(),
                            "📭 L’enchère est terminée sans aucune mise.",
                            "AUCTION_NO_BID",
                            false,
                            LocalDateTime.now()
                    )
            );

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + event.ownerId(),
                    notification
            );
        }
    }
}