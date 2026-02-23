package com.smartiadev.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ItemSummaryDto {

    private Long id;
    private Double pricePerDay;
    private Double averageRating;
}
