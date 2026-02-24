package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.RentalCancelledByUserEvent;
import com.smartiadev.base_domain_service.dto.RentalCancelledEvent;
import com.smartiadev.base_domain_service.dto.RentalEndedEvent;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class RentalCanceledConsumer {
    private final ItemRepository itemRepository;

    @KafkaListener(topics = "rental.cancelled.by.user")
    @Transactional
    public void handleRentalCanceled(RentalCancelledByUserEvent event) {

        Item item = itemRepository.findById(event.itemId())
                .orElseThrow(() ->
                        new IllegalStateException("Item not found"));

        // 🔓 rendu disponible
        item.setActive(true);

        itemRepository.save(item);

        System.out.println(
                "✅ Item " + item.getId() + " est maintenant DISPONIBLE"
        );
    }
}
