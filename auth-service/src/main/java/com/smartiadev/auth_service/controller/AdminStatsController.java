package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.AdminStats;
import com.smartiadev.auth_service.service.AdminStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @GetMapping
    public AdminStats stats() {
        return adminStatsService.getStats();
    }
}
