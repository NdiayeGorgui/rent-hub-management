package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.dto.ItemSearchRequest;
import com.smartiadev.item_service.entity.Item;
import com.smartiadev.item_service.service.ItemQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items/public")
@RequiredArgsConstructor
public class ItemPublicController {

    private final ItemQueryService itemQueryService;

    @PostMapping("/search")
    public Page<Item> searchPublishedItems(
            @RequestBody ItemSearchRequest request
    ) {
        return itemQueryService.searchPublishedItems(request);
    }
}
