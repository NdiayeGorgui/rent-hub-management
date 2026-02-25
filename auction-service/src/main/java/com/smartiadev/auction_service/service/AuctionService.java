package com.smartiadev.auction_service.service;

import com.smartiadev.auction_service.client.ItemClient;
import com.smartiadev.auction_service.client.SubscriptionClient;
import com.smartiadev.auction_service.dto.*;
import com.smartiadev.auction_service.entity.*;
import com.smartiadev.auction_service.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final SubscriptionClient subscriptionClient;
    private final ItemClient itemClient;

    public AuctionDto createAuction(CreateAuctionRequest request, UUID userId) {

        // 🔐 PREMIUM
        if (!subscriptionClient.isPremium(userId)) {
            throw new RuntimeException("Premium subscription required");
        }

        // 📦 ITEM
        ItemInternalDTO item = itemClient.getItem(request.itemId());

        if (!item.active()) {
            throw new IllegalStateException("Item is not active");
        }

        if (!item.ownerId().equals(userId)) {
            throw new IllegalStateException("Only owner can create auction");
        }

        // ❌ Une seule enchère active par item
        if (auctionRepository.existsByItemIdAndStatus(
                item.id(), AuctionStatus.OPEN)) {
            throw new IllegalStateException("Auction already exists for this item");
        }

        Auction auction = Auction.builder()
                .itemId(item.id())
                .ownerId(userId)
                .startPrice(request.startPrice())
                .currentPrice(request.startPrice())
                .startDate(LocalDateTime.now())
                .endDate(request.endDate())
                .status(AuctionStatus.OPEN)
                .build();

        return map(auctionRepository.save(auction));
    }

    @Transactional
    public void placeBid(Long auctionId, PlaceBidRequest request, UUID userId) {

        if (!subscriptionClient.isPremium(userId)) {
            throw new RuntimeException("Premium subscription required");
        }

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new RuntimeException("Auction not found"));

        // 🚫 Auction fermée
        if (auction.getStatus() != AuctionStatus.OPEN) {
            throw new IllegalStateException("Auction closed");
        }

        // 🚫 Expirée
        if (auction.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Auction expired");
        }

        // 🚫 Owner ne peut pas miser
        if (auction.getOwnerId().equals(userId)) {
            throw new IllegalStateException("Owner cannot bid on own auction");
        }

        // 🚫 Enchère trop basse
        if (request.amount() <= auction.getCurrentPrice()) {
            throw new IllegalArgumentException("Bid too low");
        }

        try {
            // 🔥 Update prix (dirty checking automatique)
            auction.setCurrentPrice(request.amount());

            // 🔥 Sauvegarde bid
            bidRepository.save(
                    Bid.builder()
                            .auctionId(auctionId)
                            .bidderId(userId)
                            .amount(request.amount())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

        } catch (OptimisticLockingFailureException e) {
            throw new IllegalStateException("Another bid was placed at the same time. Please retry.");
        }
    }

    public List<AuctionDto> getOpenAuctions() {
        return auctionRepository.findByStatus(AuctionStatus.OPEN)
                .stream()
                .map(this::map)
                .toList();
    }

    private AuctionDto map(Auction a) {
        return new AuctionDto(
                a.getId(),
                a.getItemId(),
                a.getCurrentPrice(),
                a.getEndDate(),
                a.getStatus().name()
        );
    }
}
