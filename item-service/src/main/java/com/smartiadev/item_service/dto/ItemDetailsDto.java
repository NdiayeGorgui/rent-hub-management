package com.smartiadev.item_service.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ItemDetailsDto {

    private Long itemId;
    private String title;
    private String description;
    private Long categoryId;
    private Double pricePerDay;
    private String city;
    private String address;
    private List<String> imageUrls;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Double averageRating;

    private PublisherDto publisher;
}
