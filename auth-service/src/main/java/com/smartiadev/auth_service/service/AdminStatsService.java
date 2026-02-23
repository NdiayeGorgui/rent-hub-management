package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.client.DisputeClient;
import com.smartiadev.auth_service.client.ItemClient;
import com.smartiadev.auth_service.client.RentalClient;
import com.smartiadev.auth_service.client.ReviewClient;
import com.smartiadev.auth_service.dto.AdminStats;
import com.smartiadev.auth_service.dto.DisputeStats;
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
                disputeStats
        );
    }
}
