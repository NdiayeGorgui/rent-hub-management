package com.smartiadev.review_service.dto;

import java.util.UUID;

public record ReviewDto(
        Long id,
        Long itemId,
        UUID reviewerId,
        UUID reviewedUserId,
        Integer rating,
        String comment
) {}
