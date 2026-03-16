package com.smartiadev.messaging_service.dto;



import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email,
        String username
) {}