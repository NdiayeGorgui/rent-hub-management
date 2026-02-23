package com.smartiadev.dispute_service.controller;

import com.smartiadev.dispute_service.dto.DisputeDto;
import com.smartiadev.dispute_service.dto.ResolveDisputeRequest;
import com.smartiadev.dispute_service.service.DisputeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/disputes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDisputeController {

    private final DisputeService service;

    @GetMapping
    public List<DisputeDto> all() {
        return service.all();
    }


    @PutMapping("/{id}/resolve")
    public void resolve(
            @PathVariable Long id,
            @RequestBody ResolveDisputeRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID adminId = UUID.fromString(jwt.getSubject());
        service.resolve(id, request, adminId);
    }

}

