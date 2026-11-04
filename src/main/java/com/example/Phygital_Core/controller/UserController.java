package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.dto.AuthUserDto;
import com.example.Phygital_Core.dto.RegisterPayload;
import com.example.Phygital_Core.entity.User;
import com.example.Phygital_Core.entity.UserRole;
import com.example.Phygital_Core.exception.AppException;
import com.example.Phygital_Core.repository.UserRepository;
import com.example.Phygital_Core.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    // 1. Получить всех пользователей
    @GetMapping
    public ResponseEntity<List<AuthUserDto>> getAllUsers() {
        List<AuthUserDto> users = userRepository.findAll().stream()
                .map(authService::mapToDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    // 2. Обновить пользователя (поменяли PUT на PATCH)
    @PatchMapping("/{id}")
    public ResponseEntity<AuthUserDto> updateUser(@PathVariable UUID id, @RequestBody AuthUserDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Пользователь не найден"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getRole() != null) user.setRole(UserRole.valueOf(request.getRole().name()));
        if (request.getBio() != null) user.setBio(request.getBio());

        userRepository.save(user);
        return ResponseEntity.ok(authService.mapToDto(user));
    }

    // 3. Удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 4. Сброс пароля
    @PostMapping("/{id}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Пользователь не найден"));
        user.setPassword(passwordEncoder.encode("12345678")); // Дефолтный пароль
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    // 5. Создать пользователя
    @PostMapping
    public ResponseEntity<AuthUserDto> createUser(@RequestBody RegisterPayload payload) {
        if (userRepository.existsByEmailIgnoreCase(payload.getEmail())) {
            throw new AppException(400, "Пользователь с таким email уже существует");
        }
        User user = User.builder()
                .firstName(payload.getFirstName())
                .lastName(payload.getLastName())
                .email(payload.getEmail().toLowerCase())
                .password(passwordEncoder.encode(payload.getPassword()))
                .role(UserRole.USER)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        userRepository.save(user);
        return ResponseEntity.ok(authService.mapToDto(user));
    }
}
