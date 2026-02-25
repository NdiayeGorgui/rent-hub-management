package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.client.*;
import com.smartiadev.auth_service.dto.*;
import com.smartiadev.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    private final UserRepository userRepository;
    private final ItemClient itemClient;
    private final RentalClient rentalClient;
    private final ReviewClient reviewClient;
    private final DisputeClient disputeClient;
    private final AuctionClient auctionClient;
    private final SubscriptionClient subscriptionClient;
    private final PaymentClient paymentClient;

    public AdminStats getStats() {

        // 👤 USERS
        Long totalUsers = userRepository.count();
        Long activeUsers = userRepository.countActiveUsers(); // ex: enabled=true

        // 📦 ITEMS
        Long totalItems = itemClient.countAllItems();
        Long publishedItems = itemClient.countPublishedItems();

        // 🔁 RENTALS
        Long totalRentals = rentalClient.countAllRentals();
        Long activeRentals = rentalClient.countActiveRentals();
        Double totalRevenue = rentalClient.getTotalRevenue();

        // ⭐ REVIEWS
        Long totalReviews = reviewClient.countAllReviews();
        Double avgRating = reviewClient.getPlatformAverageRating();

        // ⚖️ DISPUTES
        DisputeStats disputeStats = disputeClient.getDisputeStats();

        // 🔨 AUCTIONS
        AuctionStats auctionStats = auctionClient.getAuctionStats();

        // 💎 SUBSCRIPTIONS
        SubscriptionStats subscriptionStats = subscriptionClient.getStats();

        // 💳 PAYMENTS
        PaymentStats paymentStats = paymentClient.getStats();

        return new AdminStats(
                totalUsers,
                activeUsers,
                totalItems,
                publishedItems,
                totalRentals,
                activeRentals,
                totalRevenue,
                totalReviews,
                avgRating != null ? avgRating : 0.0,
                disputeStats,
                auctionStats,
                subscriptionStats,
                paymentStats
        );
    }
}
