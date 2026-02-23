/*
package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.RentalApprovedEvent;
import com.smartiadev.item_service.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ItemRentalConsumer {

    private final ItemService itemService;

    @KafkaListener(
            topics = "rental-approved",
            groupId = "item-service"
    )
    public void onRentalApproved(RentalApprovedEvent event) {

        System.out.println("🔒 Locking item " + event.itemId());

        itemService.deactivateFromRental(
                event.itemId(),
                event.ownerId()
        );
    }
}
*/
