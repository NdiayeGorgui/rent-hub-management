package com.smartiadev.rental_service.repository;

import com.smartiadev.rental_service.dto.RentalStatus;
import com.smartiadev.rental_service.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface RentalRepository extends JpaRepository<Rental, Long> {

    List<Rental> findByRenterId(UUID renterId);
    Page<Rental> findByRenterId(UUID renterId, Pageable pageable);


    List<Rental> findByOwnerId(UUID ownerId);

    boolean existsByItemIdAndStatusIn(
            Long itemId,
            List<RentalStatus>
                    statuses
    );

    List<Rental> findByItemIdAndStatus(
            Long itemId,
            RentalStatus status
    );

    List<Rental> findByStatusAndStartDate(
            RentalStatus status,
            LocalDate startDate
    );
    List<Rental> findByStatusAndEndDate(
            RentalStatus status,
            LocalDate endDate
    );

    @Query("""
SELECT DISTINCT r.itemId
FROM Rental r
WHERE r.status IN ('APPROVED', 'ONGOING')
AND r.startDate <= :endDate
AND r.endDate >= :startDate
""")
    List<Long> findUnavailableItemIds(
            LocalDate startDate,
            LocalDate endDate
    );


    // 🔁 total locations
    @Query("SELECT COUNT(r) FROM Rental r")
    Long countAllRentals();

    // 🔁 locations actives
    @Query("""
        SELECT COUNT(r)
        FROM Rental r
        WHERE r.status IN ('APPROVED', 'ONGOING')
    """)
    Long countActiveRentals();

    // 💰 chiffre d'affaires total
    @Query("SELECT COALESCE(SUM(r.totalPrice), 0) FROM Rental r WHERE r.status = 'ONGOING'")
    Double getTotalRevenue();
}
