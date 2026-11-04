package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.entity.Feedback;
import com.example.Phygital_Core.repository.FeedbackRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    @GetMapping
    public ResponseEntity<List<Feedback>> getAll() {
        return ResponseEntity.ok(feedbackRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Feedback> create(@RequestBody Feedback request) {
        return ResponseEntity.ok(feedbackRepository.save(request));
    }

    // Обновить статус обращения
    @PutMapping("/{id}/status")
    public ResponseEntity<Feedback> updateStatus(@PathVariable UUID id, @RequestBody java.util.Map<String, String> body) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow();
        feedback.setStatus(body.get("status"));
        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }

    // Отметить как просмотренное
    @PutMapping("/{id}/viewed")
    public ResponseEntity<Feedback> markViewed(@PathVariable UUID id) {
        Feedback feedback = feedbackRepository.findById(id).orElseThrow();
        if (feedback.getViewedAt() == null) {
            feedback.setViewedAt(LocalDateTime.now());
        }
        return ResponseEntity.ok(feedbackRepository.save(feedback));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        feedbackRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}