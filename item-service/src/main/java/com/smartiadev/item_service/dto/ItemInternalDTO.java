package com.smartiadev.item_service.dto;


import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        String title,
        UUID ownerId,
        boolean active,
        String type,
        Double pricePerDay
) {}

