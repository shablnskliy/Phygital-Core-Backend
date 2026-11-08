package com.example.Phygital_Core.service;

import com.example.Phygital_Core.dto.*;
import com.example.Phygital_Core.entity.User;
import com.example.Phygital_Core.entity.UserRole;
import com.example.Phygital_Core.exception.AppException;
import com.example.Phygital_Core.repository.UserRepository;
import com.example.Phygital_Core.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(request.getEmail().trim())
                .orElseThrow(() -> new AppException(400, "Неверный email или пароль"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(400, "Неверный email или пароль");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(token, mapToDto(user));
    }

    public AuthResponse register(RegisterPayload payload) {
        String email = payload.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new AppException(400, "Пользователь с таким email уже зарегистрирован");
        }

        User user = User.builder()
                .firstName(payload.getFirstName().trim())
                .lastName(payload.getLastName().trim())
                .email(email)
                .password(passwordEncoder.encode(payload.getPassword())) // Хешируем пароль!
                .role(UserRole.USER)
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token, mapToDto(user));
    }
    public AuthUserDto updateProfile(UUID userId, ProfilePatch patch) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(404, "Пользователь не найден"));

        if (patch.getFirstName() != null) user.setFirstName(patch.getFirstName().trim());
        if (patch.getLastName() != null) user.setLastName(patch.getLastName().trim());
        if (patch.getPhone() != null) user.setPhone(patch.getPhone());
        if (patch.getTelegram() != null) user.setTelegram(patch.getTelegram());
        if (patch.getBirthDate() != null) user.setBirthDate(patch.getBirthDate());
        if (patch.getBio() != null) user.setBio(patch.getBio());

        userRepository.save(user);
        return mapToDto(user);
    }

    public AuthUserDto getCurrentUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(404, "Пользователь не найден"));
        return mapToDto(user);
    }
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(404, "Пользователь не найден"));

        // Проверяем, что старый пароль верный
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(400, "Текущий пароль указан неверно");
        }

        // Проверяем длину нового пароля
        if (request.getNewPassword() == null || request.getNewPassword().length() < 8) {
            throw new AppException(400, "Новый пароль должен содержать не менее 8 символов");
        }

        // Шифруем и сохраняем
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    // Вспомогательный метод для конвертации Entity в DTO
    public AuthUserDto mapToDto(User user) {
        return AuthUserDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone() != null ? user.getPhone() : "")
                .telegram(user.getTelegram() != null ? user.getTelegram() : "")
                .birthDate(user.getBirthDate())
                .role(user.getRole())
                .bio(user.getBio() != null ? user.getBio() : "")
                .avatar(user.getAvatar() != null ? user.getAvatar() : "")
                .createdAt(user.getCreatedAt())
                .build();
    }
}