package com.smartiadev.rental_service.service.impl;

import com.smartiadev.base_domain_service.dto.RentalApprovedEvent;
import com.smartiadev.base_domain_service.dto.RentalCancelledByUserEvent;
import com.smartiadev.base_domain_service.dto.RentalCancelledEvent;
import com.smartiadev.rental_service.client.ItemClient;
import com.smartiadev.rental_service.client.ReviewClient;
import com.smartiadev.rental_service.dto.*;
import com.smartiadev.rental_service.entity.Rental;
import com.smartiadev.rental_service.entity.RentalStatus;
import com.smartiadev.rental_service.kafka.RentalEventProducer;
import com.smartiadev.rental_service.repository.RentalRepository;
import com.smartiadev.rental_service.service.RentalService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository repository;
    private final ItemClient itemClient;
    private final RentalEventProducer eventProducer;
    private final ReviewClient reviewClient;

    @Override
    public RentalResponseDTO create(RentalRequestDTO dto, UUID renterId) {

        long days = ChronoUnit.DAYS.between(dto.startDate(), dto.endDate());
        if (days <= 0) {
            throw new IllegalArgumentException("Invalid rental period");
        }

        ItemInternalDTO item = itemClient.getItem(dto.itemId());

        if (!item.active()) {
            throw new RuntimeException("Item not available");
        }

        UUID ownerId = item.ownerId();

        // ❌ interdit : proprio qui loue son item
        if (ownerId.equals(renterId)) {
            throw new RuntimeException("Owner cannot rent their own item");
        }

        // 🔒 verrouillage
        boolean locked = repository.existsByItemIdAndStatusIn(
                dto.itemId(),
                List.of(
                        RentalStatus.APPROVED,
                        RentalStatus.ONGOING
                )
        );

        if (locked) {
            throw new RuntimeException("Item is already rented");
        }

        double pricePerDay = item.pricePerDay();

        Rental rental = Rental.builder()
                .itemId(dto.itemId())
                .ownerId(ownerId)
                .renterId(renterId)
                .startDate(dto.startDate())
                .endDate(dto.endDate())
                .totalPrice(days * pricePerDay)
                .status(RentalStatus.CREATED)
                .build();

        return map(repository.save(rental));
    }


    @Override
    public List<RentalResponseDTO> myRentals(UUID renterId) {
        return repository.findByRenterId(renterId)
                .stream().map(this::map).toList();
    }

    @Override
    public List<RentalResponseDTO> rentalsForMyItems(UUID ownerId) {
        System.out.println("JWT OWNER = " + ownerId);
        return repository.findByOwnerId(ownerId)
                .stream().map(this::map).toList();
    }

    @Override
    public void cancel(Long rentalId, UUID userId) {
        Rental rental = repository.findById(rentalId)
                .orElseThrow();

        // Vérification de la légitimité
        if (!rental.getRenterId().equals(userId)
                && !rental.getOwnerId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        // ✅ Annuler la location
        rental.setStatus(RentalStatus.CANCELLED);
        repository.save(rental);
        // 🔄 Notifier item-service pour réactiver l'item
      /*  try {
            itemClient.setItemActive(rental.getItemId(), true);
        } catch (Exception e) {
            // ⚠️ Log erreur mais ne bloque pas l'annulation
            System.err.println("Failed to reactivate item " + rental.getItemId() + ": " + e.getMessage());
        }*/

        // 📣 Event Kafka
        eventProducer.sendRentalCancelledByUser(
                new RentalCancelledByUserEvent(
                        rental.getId(),
                        rental.getItemId(),
                        rental.getRenterId(),
                        RentalStatus.CANCELLED.name()
                )
        );
    }

    private RentalResponseDTO map(Rental r) {
        return new RentalResponseDTO(
                r.getId(),
                r.getItemId(),
                r.getOwnerId(),
                r.getRenterId(),
                r.getStartDate(),
                r.getEndDate(),
                r.getStatus().name(),
                r.getTotalPrice(),
                r.getCreatedAt()
        );
    }

    @Override
    @Transactional
    public void approve(Long rentalId, UUID ownerId) {

        Rental rental = repository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        // 🔐 sécurité propriétaire
        if (!rental.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Forbidden");
        }

        // 🚫 déjà approuvé ou en cours
        if (rental.getStatus() != RentalStatus.CREATED) {
            throw new RuntimeException("Rental cannot be approved");
        }

        // 🔒 vérifie si l’item est déjà verrouillé
        boolean alreadyLocked = repository.existsByItemIdAndStatusIn(
                rental.getItemId(),
                List.of(
                        RentalStatus.APPROVED,
                        RentalStatus.ONGOING
                )
        );

        if (alreadyLocked) {
            throw new RuntimeException("Item already rented");
        }

        // ✅ APPROUVE LA LOCATION
        rental.setStatus(RentalStatus.APPROVED);
        repository.save(rental);

        // ❌ AUTO-CANCEL des autres demandes
        List<Rental> others =
                repository.findByItemIdAndStatus(
                        rental.getItemId(),
                        RentalStatus.CREATED
                );

        others.forEach(r -> {
            if (!r.getId().equals(rental.getId())) {
                r.setStatus(RentalStatus.CANCELLED);

                eventProducer.sendRentalCancelled(
                        new RentalCancelledEvent(
                                r.getId(),
                                r.getItemId(),
                                r.getRenterId(),
                                "ITEM_ALREADY_RENTED"
                        )
                );
            }
        });


        repository.saveAll(others);

        // 📣 EVENT KAFKA
        eventProducer.sendRentalApproved(
                new RentalApprovedEvent(
                        rental.getId(),
                        rental.getItemId(),
                        rental.getOwnerId(),
                        rental.getRenterId()
                )
        );
    }
    @Override
    public RentalResponseDTO getRentalById(Long id) {
        Rental rental = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rental not found"));

        return new RentalResponseDTO(
                rental.getId(),
                rental.getItemId(),
                rental.getOwnerId(),
                rental.getRenterId(),
                rental.getStartDate(),
                rental.getEndDate(),
                rental.getStatus().name(),
                rental.getTotalPrice(),
                rental.getCreatedAt()
        );
    }
@Override
    public Page<RentedItemDTO> myRentedItems(UUID renterId, int page, int size) {

        Page<Rental> rentals = repository.findByRenterId(
                renterId,
                PageRequest.of(
                        page,
                        size,
                        Sort.by("startDate").descending()
                )
        );

        return rentals.map(rental ->
                new RentedItemDTO(
                        rental.getId(),
                        rental.getItemId(),
                        rental.getStartDate(),
                        rental.getEndDate(),
                        rental.getStatus().name()
                )
        );
    }
    @Override
    public List<Long> findUnavailableItemIds(LocalDate startDate, LocalDate endDate) {

        return repository.findUnavailableItemIds(startDate, endDate);
    }

    @Override
    public List<ItemSummaryDto> getRentedItems(UUID userId) {

        return repository.findByRenterId(userId).stream()
                .map(rental -> {

                    ItemInternalDTO item;
                    try {
                        item = itemClient.getItem(rental.getItemId());
                    } catch (Exception e) {
                        // ⚠️ item supprimé / inaccessible
                        return null;
                    }

                    Double rating;
                    try {
                        rating = reviewClient.getAverageRatingForItem(item.id());
                    } catch (Exception e) {
                        rating = 0.0;
                    }

                    return new ItemSummaryDto(
                            item.id(),
                            item.pricePerDay(),
                            rating != null ? rating : 0.0
                    );
                })
                .filter(Objects::nonNull) // ⬅️ TRÈS IMPORTANT
                .toList();
    }


}
