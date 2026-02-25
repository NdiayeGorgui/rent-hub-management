package com.smartiadev.auction_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auctions", schema = "auction_schema")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version
    private Long version;
    private Long itemId;
    private UUID ownerId;

    private Double startPrice;
    private Double currentPrice;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private AuctionStatus status;
}
