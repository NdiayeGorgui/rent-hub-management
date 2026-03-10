package com.smartiadev.payments_service.dto;


import java.util.UUID;

public record UserResponse(
        UUID id,
        String fullName,
        String email
) {}
