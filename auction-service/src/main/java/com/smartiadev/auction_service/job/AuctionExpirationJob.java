package com.smartiadev.auction_service.job;

import com.smartiadev.auction_service.kafka.AuctionEventPublisher;
import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import com.smartiadev.auction_service.entity.*;
import com.smartiadev.auction_service.repository.AuctionRepository;
import com.smartiadev.auction_service.repository.BidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuctionExpirationJob {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuctionEventPublisher eventPublisher;

    @Scheduled(fixedRate = 60000)
    public void closeExpiredAuctions() {

        var expiredAuctions =
                auctionRepository.findByStatusAndEndDateBefore(
                        AuctionStatus.OPEN,
                        LocalDateTime.now()
                );

        for (var auction : expiredAuctions) {

            auction.setStatus(AuctionStatus.CLOSED);

            bidRepository
                    .findTopByAuctionIdOrderByAmountDesc(auction.getId())
                    .ifPresentOrElse(

                            // 🏆 CAS AVEC GAGNANT
                            bid -> eventPublisher.publishAuctionClosed(
                                    new AuctionClosedEvent(
                                            auction.getId(),
                                            auction.getItemId(),
                                            auction.getOwnerId(),
                                            bid.getBidderId(),
                                            bid.getAmount()
                                    )
                            ),

                            // ❌ CAS SANS ENCHÈRE
                            () -> eventPublisher.publishAuctionClosed(
                                    new AuctionClosedEvent(
                                            auction.getId(),
                                            auction.getItemId(),
                                            auction.getOwnerId(),
                                            null,
                                            null
                                    )
                            )
                    );
        }

        auctionRepository.saveAll(expiredAuctions);
    }
}




