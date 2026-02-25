package com.smartiadev.subscription_service.job;

import com.smartiadev.subscription_service.entity.Subscription;
import com.smartiadev.subscription_service.kafka.SubscriptionEventPublisher;
import com.smartiadev.subscription_service.repository.SubscriptionRepository;
import com.smartiadev.subscription_service.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionRenewalJob {

    private final SubscriptionRepository repository;
    private final SubscriptionEventPublisher  publisher;

    /**
     * 🔄 Renouvelle automatiquement les abonnements premium
     * Tous les jours à 02:00 du matin
     */
    @Scheduled(cron = "0 0 2 * * *")
    public void renewSubscriptions() {

        var expiring =
                repository.findByEndDateBeforeAndAutoRenewTrue(LocalDateTime.now());

        for (var sub : expiring) {
            sub.setEndDate(sub.getEndDate().plusMonths(1));
            publisher.publishRenewed(
                    sub.getUserId(),
                    sub.getEndDate()
            );
        }

        repository.saveAll(expiring);
    }

}
