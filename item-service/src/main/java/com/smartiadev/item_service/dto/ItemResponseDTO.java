package com.smartiadev.item_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ItemResponseDTO {

    private Long id;
    private UUID ownerId;
    private String title;
    private String description;
    private Long categoryId;
    private Double pricePerDay;
    private String city;
    private String address;
    private List<String> imageUrls;
    private Boolean active;
    private LocalDateTime createdAt;
}
