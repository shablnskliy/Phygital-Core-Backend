package com.example.Phygital_Core.controller;
import com.example.Phygital_Core.dto.TeamDto;
import com.example.Phygital_Core.dto.TeamRequest;
import com.example.Phygital_Core.service.TeamService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TeamController {

    private final TeamService teamService;

    @PutMapping("/{id}")
    public ResponseEntity<TeamDto> update(@PathVariable UUID id, @RequestBody TeamRequest request) {
        // Просто вызываем сервис (метод updateTeam нужно добавить в TeamService)
        return ResponseEntity.ok(teamService.updateTeam(id, request));
    }

    @GetMapping
    public ResponseEntity<List<TeamDto>> getAll() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping
    public ResponseEntity<TeamDto> create(@RequestBody TeamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamService.createTeam(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }
}
