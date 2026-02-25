package com.smartiadev.auction_service.dto;


import java.util.UUID;

public record ItemInternalDTO(
        Long id,
        UUID ownerId,
        Boolean active,
        Double pricePerDay
) {}
