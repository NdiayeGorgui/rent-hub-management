package com.smartiadev.item_service.controller;

import com.smartiadev.item_service.dto.ItemResponseDTO;
import com.smartiadev.item_service.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminItemController {

    private final ItemService itemService;

    /* =====================
       LIST ALL ITEMS (ADMIN)
       ===================== */
    @GetMapping
    public List<ItemResponseDTO> listAll() {
        return itemService.findAllIncludingInactive();
    }

    /* =====================
       DEACTIVATE ITEM (ADMIN)
       ===================== */
    @PutMapping("/{id}/deactivate")
    public void deactivate(@PathVariable Long id) {
        itemService.adminDeactivate(id);
    }

    /* =====================
       ACTIVATE ITEM (ADMIN)
       ===================== */
    @PutMapping("/{id}/activate")
    public void activate(@PathVariable Long id) {
        itemService.adminActivate(id);
    }
}

