package com.smartiadev.rental_service.controller;

import com.smartiadev.rental_service.dto.ItemSummaryDto;
import com.smartiadev.rental_service.dto.RentalRequestDTO;
import com.smartiadev.rental_service.dto.RentalResponseDTO;
import com.smartiadev.rental_service.dto.RentedItemDTO;
import com.smartiadev.rental_service.service.RentalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService service;

    @PostMapping
    public RentalResponseDTO create(
            @RequestBody @Valid RentalRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID renterId = UUID.fromString(jwt.getSubject());
        return service.create(dto, renterId);
    }

    @GetMapping("/me")
    public List<RentalResponseDTO> myRentals(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID renterId = UUID.fromString(jwt.getSubject());
        return service.myRentals(renterId);
    }

    @GetMapping("/owner")
    public List<RentalResponseDTO> ownerRentals(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return service.rentalsForMyItems(ownerId);
    }

    @DeleteMapping("/{id}")
    public void cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        service.cancel(id, userId);
    }

    @PatchMapping("/{rentalId}/approve")
    public void approveRental(
            @PathVariable Long rentalId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        service.approve(rentalId, ownerId);
    }


    //Mes articles loués
    @GetMapping("/me/rented")
    public Page<RentedItemDTO> myRentedItems(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID renterId = UUID.fromString(jwt.getSubject());
        return service.myRentedItems(renterId, page, size);
    }

    @GetMapping("/user/{userId}/history")
    public List<ItemSummaryDto> getRentalHistory(
            @PathVariable UUID userId
    ) {
        return service.getRentedItems(userId);
    }

}
