package com.smartiadev.auction_service.repository;

import com.smartiadev.auction_service.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(Long auctionId);
}
