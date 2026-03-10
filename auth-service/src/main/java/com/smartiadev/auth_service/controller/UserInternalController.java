package com.smartiadev.auth_service.controller;


import com.smartiadev.auth_service.dto.UserResponse;

import com.smartiadev.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserInternalController {

    private final AuthService userService;

    @GetMapping("/internal/{id}")
    public UserResponse getUser(@PathVariable UUID id) {
        return userService.getUser(id);
    }
}
