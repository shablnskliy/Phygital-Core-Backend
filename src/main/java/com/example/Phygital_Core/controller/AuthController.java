package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.dto.*;
import com.example.Phygital_Core.service.AuthService;
import com.example.Phygital_Core.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterPayload payload) {
        return ResponseEntity.ok(authService.register(payload));
    }

    // Эндпоинт для получения профиля по токену
    @GetMapping("/me")
    public ResponseEntity<AuthUserDto> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        try {
            String token = authHeader.substring(7); // Отрезаем слово "Bearer "
            var userId = jwtService.extractUserId(token);
            return ResponseEntity.ok(authService.getCurrentUser(userId));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }
}
