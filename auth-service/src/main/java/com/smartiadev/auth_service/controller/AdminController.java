package com.smartiadev.auth_service.controller;

import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.service.AdminService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    public List<User> listUsers() {
        return adminService.getAllUsers();
    }

    @PutMapping("/{id}/suspend")
    public void suspend(@PathVariable UUID id) {
        adminService.suspendUser(id);
    }

    @PutMapping("/{id}/activate")
    public void activate(@PathVariable UUID id) {
        adminService.activateUser(id);
    }
}
