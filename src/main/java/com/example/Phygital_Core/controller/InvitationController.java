package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.entity.Invitation;
import com.example.Phygital_Core.entity.Team;
import com.example.Phygital_Core.entity.TeamMember;
import com.example.Phygital_Core.entity.User;
import com.example.Phygital_Core.repository.InvitationRepository;
import com.example.Phygital_Core.repository.NotificationRepository;
import com.example.Phygital_Core.repository.TeamRepository;
import com.example.Phygital_Core.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/invitations")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class InvitationController {

    private final InvitationRepository invitationRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository; // <-- ДОБАВИЛИ

    @GetMapping
    public ResponseEntity<List<Invitation>> getAll() {
        return ResponseEntity.ok(invitationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Invitation> create(@RequestBody Invitation request) {
        request.setId(null);
        request.setStatus("pending");
        request.setCreatedAt(LocalDateTime.now());
        return ResponseEntity.ok(invitationRepository.save(request));
    }

    @PutMapping("/{id}/status")
    @Transactional
    public ResponseEntity<Invitation> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        Invitation invite = invitationRepository.findById(id).orElseThrow();
        String newStatus = body.get("status");
        invite.setStatus(newStatus);
        invite.setRespondedAt(LocalDateTime.now());

        // Если приглашение ПРИНЯТО — добавляем игрока в команду
        if ("accepted".equals(newStatus)) {
            Team team = teamRepository.findById(invite.getTeamId()).orElse(null);
            User user = userRepository.findById(invite.getRecipientId()).orElse(null);

            if (team != null && user != null) {
                boolean alreadyInTeam = team.getMembers().stream()
                        .anyMatch(m -> m.getUserId() != null && m.getUserId().equals(user.getId()));

                if (!alreadyInTeam) {
                    TeamMember newMember = new TeamMember();
                    newMember.setName(user.getFirstName() + " " + user.getLastName());
                    newMember.setCaptain(false);
                    newMember.setUserId(user.getId());
                    newMember.setTeam(team);

                    team.getMembers().add(newMember);
                    teamRepository.save(team);
                }
            }
        }

        // Помечаем связанное уведомление как прочитанное
        notificationRepository.findAll().stream()
                .filter(n -> n.getInvitationId() != null && n.getInvitationId().equals(invite.getId()))
                .forEach(n -> {
                    n.setRead(true);
                    notificationRepository.save(n);
                });

        return ResponseEntity.ok(invitationRepository.save(invite));
    }
}