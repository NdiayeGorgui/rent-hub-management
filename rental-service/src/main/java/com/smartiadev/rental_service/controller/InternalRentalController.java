package com.smartiadev.rental_service.controller;

import com.smartiadev.rental_service.dto.RentalResponseDTO;
import com.smartiadev.rental_service.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rentals/internal")
@RequiredArgsConstructor
public class InternalRentalController {

    private final RentalService rentalService;


    @GetMapping("/{id}")
    public RentalResponseDTO getRentalInternal(@PathVariable("id") Long id) {
        return rentalService.getRentalById(id);
    }

    /**
     * Retourne les IDs des items déjà loués sur une période
     */
    @GetMapping("/unavailable")
    public List<Long> getUnavailableItems(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {
        return rentalService.findUnavailableItemIds(startDate, endDate);
    }
}
