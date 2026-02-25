package com.smartiadev.auth_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "subscription-service")
public interface SubscriptionClient {

    @GetMapping("/api/subscriptions/internal/{userId}")
    boolean isPremium(@PathVariable UUID userId);
}

