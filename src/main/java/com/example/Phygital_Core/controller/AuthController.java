package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.dto.*;
import com.example.Phygital_Core.service.AuthService;
import com.example.Phygital_Core.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;
import java.util.UUID;

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

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        // Достаем ID текущего пользователя из SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Не авторизован"));
        }

        UUID userId = (UUID) auth.getPrincipal(); // Так мы сохраняли ID в JwtAuthFilter
        authService.changePassword(userId, request);

        return ResponseEntity.ok(Map.of("message", "Пароль успешно изменен"));
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
    @PutMapping("/me")
    public ResponseEntity<AuthUserDto> updateCurrentUser(@RequestBody ProfilePatch request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }

        UUID userId = (UUID) auth.getPrincipal();
        AuthUserDto updatedUser = authService.updateProfile(userId, request);
        return ResponseEntity.ok(updatedUser);
    }
}
