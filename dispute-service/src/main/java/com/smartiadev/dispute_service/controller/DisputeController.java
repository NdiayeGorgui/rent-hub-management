package com.smartiadev.dispute_service.controller;

import com.smartiadev.dispute_service.dto.CreateDisputeRequest;
import com.smartiadev.dispute_service.dto.DisputeDto;
import com.smartiadev.dispute_service.service.DisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/disputes")
@RequiredArgsConstructor
public class DisputeController {

    private final DisputeService service;

    @PostMapping
    public DisputeDto create(
            @RequestBody CreateDisputeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return service.create(request, userId);
    }

    @GetMapping("/my")
    public List<DisputeDto> my(@AuthenticationPrincipal Jwt jwt) {
        return service.myDisputes(UUID.fromString(jwt.getSubject()));
    }
}

