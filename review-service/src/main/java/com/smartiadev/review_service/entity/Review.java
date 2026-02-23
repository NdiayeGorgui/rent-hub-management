package com.smartiadev.review_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "reviews",schema = "review_schema",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"rentalId"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Location
    @Column(nullable = false, unique = true)
    private Long rentalId;

    // 🔗 Article
    @Column(nullable = false)
    private Long itemId;

    // 🔗 Auteur de l’avis
    @Column(nullable = false)
    private UUID reviewerId;

    // 🔗 Personne notée
    @Column(nullable = false)
    private UUID reviewedUserId;

    // ⭐ Note
    @Column(nullable = false)
    private Integer rating; // 1..5

    // 💬 Commentaire
    @Column(length = 1000)
    private String comment;

    // 🕒 Audit
    private LocalDateTime createdAt;
}

