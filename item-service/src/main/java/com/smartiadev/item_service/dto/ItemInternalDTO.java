package com.smartiadev.item_service.dto;


import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        UUID ownerId,
        boolean active,
        double pricePerDay
) {}

