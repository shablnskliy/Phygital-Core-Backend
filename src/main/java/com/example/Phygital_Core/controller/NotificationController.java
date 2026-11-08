package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.entity.Notification;
import com.example.Phygital_Core.repository.NotificationRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    public ResponseEntity<List<Notification>> getMyNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(401).build();
        }
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(notificationRepository.findByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Notification> create(@RequestBody Notification request) {
        request.setId(null);
        return ResponseEntity.ok(notificationRepository.save(request));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable UUID id) {
        Notification n = notificationRepository.findById(id).orElseThrow();
        n.setRead(true);
        return ResponseEntity.ok(notificationRepository.save(n));
    }
}
