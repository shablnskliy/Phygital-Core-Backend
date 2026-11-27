package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.entity.Notification;
import com.example.Phygital_Core.entity.TournamentApplication;
import com.example.Phygital_Core.repository.NotificationRepository;
import com.example.Phygital_Core.repository.TeamRepository;
import com.example.Phygital_Core.repository.TournamentApplicationRepository;
import com.example.Phygital_Core.repository.TournamentRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.example.Phygital_Core.entity.Notification;
import com.example.Phygital_Core.entity.TournamentApplication;
import com.example.Phygital_Core.repository.NotificationRepository;
import com.example.Phygital_Core.repository.TeamRepository;
import com.example.Phygital_Core.repository.TournamentApplicationRepository;
import com.example.Phygital_Core.repository.TournamentRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/tournament-applications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TournamentApplicationController {

    private final TeamRepository teamRepository;
    private final TournamentRepository tournamentRepository;
    private final NotificationRepository notificationRepository;
    private final TournamentApplicationRepository applicationRepository;

    @GetMapping
    public ResponseEntity<List<TournamentApplication>> getAll() {
        return ResponseEntity.ok(applicationRepository.findAll());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<TournamentApplication> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        TournamentApplication app = applicationRepository.findById(id).orElseThrow();
        String newStatus = body.get("status");
        app.setStatus(newStatus);
        TournamentApplication savedApp = applicationRepository.save(app);

        // Создаем уведомление для капитана команды
        try {
            var team = teamRepository.findById(app.getTeamId()).orElse(null);
            var tournament = tournamentRepository.findById(app.getTournamentId()).orElse(null);

            if (team != null && tournament != null) {
                UUID captainId = team.getMembers().stream()
                        .filter(m -> m.isCaptain() && m.getUserId() != null)
                        .map(m -> m.getUserId())
                        .findFirst()
                        .orElse(null);

                if (captainId != null) {
                    Notification notification = Notification.builder()
                            .userId(captainId)
                            .type("tournament_result")
                            .title(newStatus.equals("APPROVED") ? "Заявка одобрена" : "Заявка отклонена")
                            .message(String.format(
                                    "Ваша заявка на турнир «%s» была %s администратором.",
                                    tournament.getName(),
                                    newStatus.equals("APPROVED") ? "одобрена" : "отклонена"
                            ))
                            .isRead(false)
                            .createdAt(java.time.LocalDateTime.now())
                            .build();
                    notificationRepository.save(notification);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка создания уведомления: " + e.getMessage());
        }

        return ResponseEntity.ok(savedApp);
    }
}