package com.smartiadev.auction_service.repository;

import com.smartiadev.auction_service.entity.Auction;
import com.smartiadev.auction_service.entity.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

    List<Auction> findByStatus(AuctionStatus status);
    boolean existsByItemIdAndStatus(Long itemId, AuctionStatus status);
    List<Auction> findByStatusAndEndDateBefore(
            AuctionStatus status,
            LocalDateTime date
    );


}
