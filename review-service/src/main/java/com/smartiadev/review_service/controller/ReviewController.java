package com.smartiadev.review_service.controller;

import com.smartiadev.review_service.dto.CreateReviewRequest;
import com.smartiadev.review_service.dto.ReviewDto;
import com.smartiadev.review_service.entity.Review;
import com.smartiadev.review_service.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review createReview(
            @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID reviewerId = UUID.fromString(jwt.getSubject());
        return reviewService.createReview(request, reviewerId);
    }


    // ⭐ Avis pour un article
    @GetMapping("/item/{itemId}")
    public List<ReviewDto> getReviewsByItem(@PathVariable Long itemId) {
        return reviewService.getReviewsByItemId(itemId);
    }

    // ⭐ Avis pour un utilisateur
    @GetMapping("/user/{userId}")
    public List<ReviewDto> getReviewsByUser(@PathVariable UUID userId) {
        return reviewService.getReviewsByUser(userId);
    }

    @GetMapping("/item/{itemId}/average")
    public Double getAverageRatingForItem(@PathVariable Long itemId) {
        return reviewService.getAverageRatingForItem(itemId);
    }

    @GetMapping("/user/{userId}/average")
    public Double getAverageRatingForUser(@PathVariable UUID userId) {
        return reviewService.getAverageRatingForUser(userId);
    }

    @GetMapping("/user/{userId}/count")
    public Long getReviewsCountForUser(@PathVariable UUID userId) {
        return reviewService.getReviewsCountForUser(userId);
    }

    @GetMapping("/items/min-rating")
    public List<Long> getItemsWithMinRating(
            @RequestParam Double minRating
    ) {
        return reviewService.getItemIdsWithMinRating(minRating);
    }

    @GetMapping("/items/ratings")
    public Map<Long, Double> getItemsRatings() {
        return reviewService.getItemsAverageRatings();
    }


}
