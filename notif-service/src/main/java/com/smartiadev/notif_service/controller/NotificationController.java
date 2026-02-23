package com.smartiadev.notif_service.controller;

import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository repository;

    @GetMapping("/me")
    public List<Notification> myNotifications(
            @AuthenticationPrincipal Jwt  jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Notification notification = repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Notification not found"));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalStateException("Forbidden");
        }

        notification.setRead(true);
        repository.save(notification);
    }
    @PutMapping("/me/read-all")
    public void markAllAsRead(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        repository.markAllAsRead(userId);
    }

}

