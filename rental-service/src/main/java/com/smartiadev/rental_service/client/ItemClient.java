package com.smartiadev.rental_service.client;

import com.smartiadev.rental_service.dto.ItemInternalDTO;
import com.smartiadev.rental_service.dto.RentalResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "item-service")
public interface ItemClient {

    @GetMapping("/api/items/internal/{id}")
    ItemInternalDTO getItem(@PathVariable("id") Long itemId);
}

