package com.smartiadev.messaging_service.repository;

import com.smartiadev.messaging_service.entity.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

   /* @Query("""
        SELECT c FROM Conversation c
        WHERE (
            (c.user1Id = :userA AND c.user2Id = :userB)
            OR
            (c.user1Id = :userB AND c.user2Id = :userA)
        )
        AND c.itemId = :itemId
    """)
    Optional<Conversation> findConversationBetweenUsersAndItem(
            UUID userA,
            UUID userB,
            Long itemId
    );*/

    @Query("""
SELECT c FROM Conversation c
WHERE
(
 (c.user1Id = :user1 AND c.user2Id = :user2)
 OR
 (c.user1Id = :user2 AND c.user2Id = :user1)
)
AND c.itemId = :itemId
""")
    Optional<Conversation> findConversationBetweenUsersAndItem(
            UUID user1,
            UUID user2,
            Long itemId
    );

    List<Conversation> findByUser1IdOrUser2IdOrderByLastMessageAtDesc(
            UUID user1Id,
            UUID user2Id
    );
}