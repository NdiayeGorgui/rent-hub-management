package com.smartiadev.subscription_service.job;

import com.smartiadev.subscription_service.entity.SubscriptionStatus;
import com.smartiadev.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class SubscriptionExpirationJob {

    private final SubscriptionRepository repository;

    @Scheduled(cron = "0 0 3 * * *")
    public void expireGracePeriodSubscriptions() {

        var expired =
                repository.findByStatusAndEndDateBefore(
                        SubscriptionStatus.GRACE_PERIOD,
                        LocalDateTime.now()
                );

        expired.forEach(sub -> sub.setStatus(SubscriptionStatus.EXPIRED));
        repository.saveAll(expired);
    }
}

