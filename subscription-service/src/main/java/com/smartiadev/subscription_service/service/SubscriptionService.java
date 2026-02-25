package com.smartiadev.subscription_service.service;

import com.smartiadev.subscription_service.entity.Subscription;
import com.smartiadev.subscription_service.entity.SubscriptionStatus;
import com.smartiadev.subscription_service.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository repository;

    public boolean isPremium(UUID userId) {
        return repository.existsByUserIdAndStatus(
                userId, SubscriptionStatus.ACTIVE
        );
    }

    public Subscription subscribe(UUID userId) {

        repository.findByUserId(userId).ifPresent(sub -> {
            if (sub.getStatus() == SubscriptionStatus.ACTIVE) {
                throw new IllegalStateException("Already subscribed");
            }
        });

        Subscription subscription = Subscription.builder()
                .userId(userId)
                .status(SubscriptionStatus.ACTIVE)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .autoRenew(true)
                .build();

        return repository.save(subscription);
    }

    public void cancel(UUID userId) {
        Subscription sub = repository.findByUserId(userId)
                .orElseThrow();

        sub.setStatus(SubscriptionStatus.CANCELED);
        sub.setAutoRenew(false);

        repository.save(sub);
    }

    public void handlePaymentFailure(UUID userId) {

        repository.findByUserId(userId).ifPresent(sub -> {
            sub.setStatus(SubscriptionStatus.GRACE_PERIOD);
            repository.save(sub);
        });
    }

}
