package com.smartiadev.item_service.service.impl;

import com.smartiadev.item_service.client.AuthClient;
import com.smartiadev.item_service.client.RentalClient;
import com.smartiadev.item_service.client.ReviewClient;
import com.smartiadev.item_service.dto.*;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.repository.ItemRepository;
import com.smartiadev.item_service.repository.specification.ItemSpecifications;
import com.smartiadev.item_service.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final ReviewClient reviewClient;
    private final RentalClient rentalClient;
    private final AuthClient authClient;
    /* =====================
       CREATE ITEM
       ===================== */
    @Override
    public ItemResponseDTO create(ItemRequestDTO dto, UUID ownerId) {

        Item item = Item.builder()
                .ownerId(ownerId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .categoryId(dto.getCategoryId())
                .pricePerDay(dto.getPricePerDay())
                .city(dto.getCity())
                .address(dto.getAddress())
                .imageUrls(dto.getImageUrls())
                .active(true)
                .build();

        Item saved = repository.save(item);
        return map(saved);
    }

    /* =====================
       FIND ALL ACTIVE
       ===================== */
    @Override
    public List<ItemResponseDTO> findAllActive() {
        return repository.findByActiveTrue()
                .stream()
                .map(this::map)
                .toList();
    }

    /* =====================
       FIND BY ID
       ===================== */
    @Override
    public ItemResponseDTO findById(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found with id " + id)
                );

        return map(item);
    }

    /* =====================
       FIND BY OWNER
       ===================== */
    @Override
    public List<ItemResponseDTO> findByOwner(UUID ownerId) {
        return repository.findByOwnerId(ownerId)
                .stream()
                .map(this::map)
                .toList();
    }

    /* =====================
       DEACTIVATE ITEM
       ===================== */
    @Override
    public void deactivate(Long itemId, UUID ownerId) {

        Item item = repository.findById(itemId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Item not found")
                );

        if (!item.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Forbidden: not item owner");
        }

        item.setActive(false);
        repository.save(item);
    }

    @Override
    public void deactivateFromRental(Long itemId, UUID ownerId) {

        Item item = repository.findById(itemId)
                .orElseThrow();

        if (!item.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Forbidden");
        }

        item.setActive(false);
        repository.save(item);
    }


    /* =====================
       MAPPING
       ===================== */
    private ItemResponseDTO map(Item item) {
        return new ItemResponseDTO(
                item.getId(),
                item.getOwnerId(),
                item.getTitle(),
                item.getDescription(),
                item.getCategoryId(),
                item.getPricePerDay(),
                item.getCity(),
                item.getAddress(),
                item.getImageUrls(),
                item.getActive(),
                item.getCreatedAt()
        );
    }
   /* public ItemDetailsDto getItemDetails(Long itemId) {

        Double avgRating = reviewClient.getAverageRatingForItem(itemId);

        return ItemDetailsDto.builder()
                .itemId(itemId)
                .averageRating(avgRating != null ? avgRating : 0.0)
                .build();
    }*/

    @Override
    public List<ItemResponseDTO> findAllIncludingInactive() {
        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public void adminDeactivate(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setActive(false);
        repository.save(item);
    }

    @Override
    public void adminActivate(Long id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        item.setActive(true);
        repository.save(item);
    }

    @Override
    public Page<ItemResponseDTO> myPublishedItems(UUID ownerId, int page, int size) {

        Page<Item> items = repository.findByOwnerId(
                ownerId,
                PageRequest.of(
                        page,
                        size,
                        Sort.by("createdAt").descending()
                )
        );

        return items.map(this::map);
    }

    @Override
    public Page<ItemSearchResponseDto> searchItems(
            String city,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            LocalDate startDate,
            LocalDate endDate,
            Double minRating,
            Pageable pageable
    ) {

    /* =========================
       1️⃣ ITEMS INDISPONIBLES
       ========================= */
        List<Long> unavailableItemIds = null;

        if (startDate != null && endDate != null) {
            unavailableItemIds =
                    rentalClient.getUnavailableItems(startDate, endDate);
        }

    /* =========================
       2️⃣ FILTRE NOTE MINIMALE
       ========================= */
        List<Long> ratedItemIds = null;

        if (minRating != null) {

            ratedItemIds =
                    reviewClient.getItemIdsWithMinRating(minRating);

            // aucun item correspond
            if (ratedItemIds == null || ratedItemIds.isEmpty()) {
                return Page.empty(pageable);
            }
        }

    /* =========================
       3️⃣ CONSTRUCTION SPECIFICATION
       ========================= */
        Specification<Item> spec =
                Specification.where(
                                ItemSpecifications.isActive(true)
                        )
                        .and(ItemSpecifications.hasCity(city))
                        .and(ItemSpecifications.hasCategory(categoryId))
                        .and(ItemSpecifications.priceBetween(minPrice, maxPrice))
                        .and(ItemSpecifications.excludeIds(unavailableItemIds))
                        .and(ItemSpecifications.includeIds(ratedItemIds));

    /* =========================
       4️⃣ REQUÊTE DB
       ========================= */
        Page<Item> itemsPage =
                repository.findAll(spec, pageable);

    /* =========================
       5️⃣ RÉCUPÉRATION RATINGS
       ========================= */
        Map<Long, Double> ratings =
                reviewClient.getItemsRatings();

    /* =========================
       6️⃣ MAPPING DTO
       ========================= */
        return itemsPage.map(item ->
                new ItemSearchResponseDto(
                        item.getId(),
                        item.getTitle(),
                        item.getCity(),
                        item.getPricePerDay(),
                        ratings.getOrDefault(item.getId(), 0.0)
                )
        );
    }


@Override
    public List<ItemSummaryDto> getPublishedItemsByUser(UUID userId) {

        Map<Long, Double> ratings = reviewClient.getItemsRatings();

        return repository.findByOwnerId(userId).stream()
                .map(item -> new ItemSummaryDto(
                        item.getId(),
                        item.getPricePerDay(),
                        ratings.getOrDefault(item.getId(), 0.0)
                ))
                .toList();
    }

    @Override
    public ItemDetailsDto getItemDetails(Long itemId) {

        // 1️⃣ Item (DB locale)
        Item item = repository.findById(itemId)
                .orElseThrow(() ->
                        new RuntimeException("Item not found"));

        // 2️⃣ Rating de l’item
        Double itemRating =
                reviewClient.getAverageRatingForItem(itemId);

        Double safeItemRating =
                itemRating != null ? itemRating : 0.0;

        // 3️⃣ Publisher (ownerId)
        UserProfileInternalDto user =
                authClient.getUserProfile(item.getOwnerId());

        PublisherDto publisher = PublisherDto.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .city(user.getCity())
                .averageRating(
                        user.getAverageRating() != null
                                ? user.getAverageRating()
                                : 0.0
                )
                .reviewsCount(
                        user.getReviewsCount() != null
                                ? user.getReviewsCount()
                                : 0L
                )
                .badge(user.getBadge())
                .build();

        // 4️⃣ Composition finale
        return ItemDetailsDto.builder()
                .itemId(item.getId())
                .title(item.getTitle())
                .description(item.getDescription())
                .categoryId(item.getCategoryId())
                .pricePerDay(item.getPricePerDay())
                .city(item.getCity())
                .address(item.getAddress())
                .imageUrls(item.getImageUrls())
                .active(item.getActive())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .averageRating(safeItemRating)
                .publisher(publisher)
                .build();
    }


}
