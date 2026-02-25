package com.smartiadev.subscription_service.repository;

import com.smartiadev.subscription_service.entity.Subscription;
import com.smartiadev.subscription_service.entity.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository
        extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserId(UUID userId);

    boolean existsByUserIdAndStatus(UUID userId, SubscriptionStatus status);
    List<Subscription> findByEndDateBeforeAndAutoRenewTrue(LocalDateTime date);

    List<Subscription> findByStatusAndEndDateBefore(
            SubscriptionStatus status,
            LocalDateTime date
    );


}

