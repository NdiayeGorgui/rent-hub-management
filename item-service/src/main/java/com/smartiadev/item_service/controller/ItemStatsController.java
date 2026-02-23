package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.service.ItemStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/items/stats")
@RequiredArgsConstructor
public class ItemStatsController {

    private final ItemStatsService itemStatsService;

    @GetMapping("/count")
    public Long countAllItems() {
        return itemStatsService.countAllItems();
    }

    @GetMapping("/count/published")
    public Long countPublishedItems() {
        return itemStatsService.countPublishedItems();
    }
}
