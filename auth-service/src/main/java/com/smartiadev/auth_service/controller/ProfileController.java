package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.dto.UserProfileDto;
import com.smartiadev.auth_service.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 🔐 Profil PRIVÉ (utilisateur connecté)
     */
    @GetMapping("/me")
    public UserProfileDto getMyProfile(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = UUID.fromString(jwt.getSubject());
        return profileService.getMyProfile(userId);
    }

    /**
     * 👤 Profil PUBLIC
     */
    @GetMapping("/{userId}")
    public UserProfileDto getPublicProfile(@PathVariable UUID userId) {
        return profileService.getPublicProfile(userId);
    }



}


