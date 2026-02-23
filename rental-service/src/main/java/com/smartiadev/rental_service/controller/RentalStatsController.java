package com.smartiadev.rental_service.controller;

import com.smartiadev.rental_service.service.RentalStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/rentals/stats")
@RequiredArgsConstructor
public class RentalStatsController {

    private final RentalStatsService rentalStatsService;

    @GetMapping("/count")
    public Long countAllRentals() {
        return rentalStatsService.countAllRentals();
    }

    @GetMapping("/count/active")
    public Long countActiveRentals() {
        return rentalStatsService.countActiveRentals();
    }

    @GetMapping("/revenue")
    public Double getTotalRevenue() {
        return rentalStatsService.getTotalRevenue();
    }
}
