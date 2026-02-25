package com.smartiadev.auction_service.kafka;


import com.smartiadev.base_domain_service.dto.AuctionClosedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishAuctionClosed(AuctionClosedEvent event) {
        kafkaTemplate.send("auction.closed", event);
    }
}
