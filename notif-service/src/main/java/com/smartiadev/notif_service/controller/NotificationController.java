package com.smartiadev.notif_service.controller;

import com.smartiadev.notif_service.entity.Notification;
import com.smartiadev.notif_service.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Notifications", description = "Endpoints to manage user notifications")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationRepository repository;

    /* =====================
       GET MY NOTIFICATIONS
       ===================== */
    @Operation(
            summary = "Get my notifications",
            description = "Retrieve all notifications for the authenticated user, ordered by creation date descending."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Notification.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public List<Notification> myNotifications(
            @AuthenticationPrincipal Jwt  jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /* =====================
       MARK A NOTIFICATION AS READ
       ===================== */
    @Operation(
            summary = "Mark a notification as read",
            description = "Mark a single notification as read for the authenticated user."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification marked as read"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - notification does not belong to user"),
            @ApiResponse(responseCode = "404", description = "Notification not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
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

