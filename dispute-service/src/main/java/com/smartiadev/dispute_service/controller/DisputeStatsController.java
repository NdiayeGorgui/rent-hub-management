package com.smartiadev.dispute_service.controller;

import com.smartiadev.dispute_service.dto.DisputeStats;
import com.smartiadev.dispute_service.service.DisputeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/disputes/stats")
@RequiredArgsConstructor
public class DisputeStatsController {

    private final DisputeStatsService service;

    @GetMapping
    public DisputeStats stats() {
        return service.getStats();
    }
}
