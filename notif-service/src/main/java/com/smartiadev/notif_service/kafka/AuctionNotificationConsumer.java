package com.smartiadev.notif_service.kafka;

import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionNotificationConsumer {

    private final NotificationRepository repository;

    @KafkaListener(
            topics = "auction.closed",
            groupId = "notification-group"
    )
    public void onAuctionClosed(AuctionClosedEvent event) {
        if (event.winnerId() != null) {
            repository.save(
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

            System.out.println(
                    "🏆 Notification gagnant enchère envoyée : " + event.winnerId()
            );
        }else {
            // 📭 Aucune enchère → notifier le propriétaire (optionnel)
            repository.save(
                    new Notification(
                            null,
                            event.ownerId(),
                            "📭 L’enchère est terminée sans aucune mise.",
                            "AUCTION_NO_BID",
                            false,
                            LocalDateTime.now()
                    )
            );

        }

    }
}
