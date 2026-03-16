package com.smartiadev.base_domain_service.dto;

import java.util.UUID;

public record AuctionFeeRefundedEvent(
        Long auctionId,
        UUID ownerId
) {}