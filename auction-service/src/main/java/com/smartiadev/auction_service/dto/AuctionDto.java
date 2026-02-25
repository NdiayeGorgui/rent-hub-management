package com.smartiadev.auction_service.dto;

import java.time.LocalDateTime;

public record AuctionDto(
        Long id,
        Long itemId,
        Double currentPrice,
        LocalDateTime endDate,
        String status
) {}

