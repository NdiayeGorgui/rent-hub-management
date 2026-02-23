package com.smartiadev.auth_service.service;

import com.smartiadev.auth_service.dto.request.LoginRequest;
import com.smartiadev.auth_service.dto.request.RegisterRequest;
import com.smartiadev.auth_service.dto.response.AuthResponse;
import com.smartiadev.auth_service.entity.User;
import com.smartiadev.auth_service.repository.UserRepository;
import com.smartiadev.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already used");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .phone(request.phone())
                .city(request.city())
                .createdAt(LocalDateTime.now())
                .roles(Set.of("ROLE_USER"))
                .enabled(true)
                .build();

        userRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user));
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Account suspended");
        }


        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}
