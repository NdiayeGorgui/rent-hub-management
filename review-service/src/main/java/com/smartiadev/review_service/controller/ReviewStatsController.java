package com.smartiadev.review_service.controller;

import com.smartiadev.review_service.service.ReviewStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews/stats")
@RequiredArgsConstructor
public class ReviewStatsController {

    private final ReviewStatsService reviewStatsService;

    @GetMapping("/count")
    public Long countAllReviews() {
        return reviewStatsService.countAllReviews();
    }

    @GetMapping("/average/platform")
    public Double getPlatformAverageRating() {
        return reviewStatsService.getPlatformAverageRating();
    }
}
