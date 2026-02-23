package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.dto.*;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService service;

    /* =====================
       CREATE ITEM (JWT)
       ===================== */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<ItemResponseDTO> create(
            @RequestBody @Valid ItemRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        System.out.println("JWT SUBJECT = " + jwt.getSubject());

        return ResponseEntity.ok(service.create(dto, ownerId));
    }

    /* =====================
       LIST ACTIVE ITEMS
       ===================== */
    @GetMapping
    public List<ItemResponseDTO> list() {
        return service.findAllActive();
    }

    /* =====================
       GET ITEM BY ID
       ===================== */
    @GetMapping("/{id}")
    public ItemResponseDTO get(@PathVariable Long id) {
        return service.findById(id);
    }

    /* =====================
       GET MY ITEMS (JWT)
       ===================== */
    @GetMapping("/me")
    public List<ItemResponseDTO> myItems(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return service.findByOwner(ownerId);
    }
    /* =====================
          GET MY PUBLISHED ITEMS (JWT)
          ===================== */
    @GetMapping("/me/published")
    public Page<ItemResponseDTO> myPublishedItems(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        return service.myPublishedItems(ownerId, page, size);
    }


    /* =====================
       DEACTIVATE ITEM (OWNER ONLY)
       ===================== */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID ownerId = UUID.fromString(jwt.getSubject());
        service.deactivate(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/{id}")
    public ItemInternalDTO getInternalItem(@PathVariable Long id) {
        ItemResponseDTO item = service.findById(id);

        return new ItemInternalDTO(
                item.getId(),
                item.getOwnerId(),
                item.getActive(),
                item.getPricePerDay()
        );
    }

    /**
     * Recherche d'articles disponibles avec filtres + tri
     */
    @GetMapping("/search")
    public Page<ItemSearchResponseDto> searchItems(

            // 📍 LOCALISATION
            @RequestParam(required = false) String city,

            // 🗂️ CATÉGORIE
            @RequestParam(required = false) Long categoryId,

            // 💰 PRIX
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,

            // ⭐ NOTE MINIMALE
            @RequestParam(required = false) Double minRating,

            // 📅 DISPONIBILITÉ
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,

            // 🔽 TRI
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,

            // 📄 PAGINATION
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

       /* Sort sort = Sort.by(
                Sort.Direction.fromString(direction),
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);*/
        Sort primarySort = Sort.by(
                Sort.Direction.fromString(direction),
                sortBy
        );

        Sort stableSort = primarySort.and(
                Sort.by(Sort.Direction.DESC, "id")
        );

        Pageable pageable = PageRequest.of(page, size, stableSort);

        return service.searchItems(
                city,
                categoryId,
                minPrice,
                maxPrice,
                startDate,
                endDate,
                minRating,
                pageable
        );
    }

    @GetMapping("/user/{userId}/published")
    public List<ItemSummaryDto> getPublishedItems(
            @PathVariable UUID userId
    ) {
        return service.getPublishedItemsByUser(userId);
    }

    @GetMapping("/{id}/details")
    public ItemDetailsDto getItemDetails(@PathVariable Long id) {
        return service.getItemDetails(id);
    }

}
