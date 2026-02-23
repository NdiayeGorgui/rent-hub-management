package com.smartiadev.dispute_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @PutMapping("/api/admin/users/{id}/suspend")
    void suspend(@PathVariable UUID id);
}

