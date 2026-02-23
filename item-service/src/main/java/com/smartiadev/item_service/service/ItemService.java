package com.smartiadev.item_service.service;

import com.smartiadev.item_service.dto.*;
import com.smartiadev.item_service.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ItemService {

    ItemResponseDTO create(ItemRequestDTO dto, UUID ownerId);

    List<ItemResponseDTO> findAllActive();

    ItemResponseDTO findById(Long id);

    List<ItemResponseDTO> findByOwner(UUID ownerId);

    void deactivate(Long itemId, UUID ownerId);
    void deactivateFromRental(Long itemId, UUID ownerId);

    List<ItemResponseDTO> findAllIncludingInactive();
    void adminDeactivate(Long id);
    void adminActivate(Long id);
    Page<ItemResponseDTO> myPublishedItems(UUID ownerId, int page, int size);

    Page<ItemSearchResponseDto> searchItems(
            String city,
            Long categoryId,
            Double minPrice,
            Double maxPrice,
            LocalDate startDate,
            LocalDate endDate,
            Double minRating,
            Pageable pageable
    );

    List<ItemSummaryDto> getPublishedItemsByUser(UUID userId);
    ItemDetailsDto getItemDetails(Long itemId);
}
