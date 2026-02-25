package com.smartiadev.auction_service.dto;

import java.time.LocalDateTime;

public record CreateAuctionRequest(
        Long itemId,
        Double startPrice,
        LocalDateTime endDate
) {}

