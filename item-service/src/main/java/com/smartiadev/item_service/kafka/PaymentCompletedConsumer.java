package com.smartiadev.item_service.kafka;

import com.smartiadev.base_domain_service.dto.PaymentCompletedEvent;
import com.smartiadev.base_domain_service.model.PaymentType;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.apache.kafka.common.requests.DeleteAclsResponse.log;

@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {
    private final ItemRepository repository;

    @KafkaListener(
            topics = "payment.completed",
            groupId = "item-service"
    )
    public void onPaymentCompleted(PaymentCompletedEvent event) {

        log.info("Payment event received: {}", event);

        if (event.type() != PaymentType.AUCTION_FEE) {
            return;
        }

        if (event.itemId() == null) {
            log.warn("Payment event without itemId");
            return;
        }

        Optional<Item> optionalItem = repository.findById(event.itemId());

        if (optionalItem.isEmpty()) {
            log.warn("Item not found: {}", event.itemId());
            return;
        }

        Item item = optionalItem.get();

        item.setActive(true);

        repository.save(item);

        log.info("Item activated: {}", item.getId());
    }
}
