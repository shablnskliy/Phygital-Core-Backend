package com.example.Phygital_Core.controller;

import com.example.Phygital_Core.dto.TournamentDto;
import com.example.Phygital_Core.dto.TournamentRequest;
import com.example.Phygital_Core.service.TournamentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<TournamentDto>> getAll() {
        return ResponseEntity.ok(tournamentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDto> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(tournamentService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TournamentDto> create(@RequestBody TournamentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tournamentService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TournamentDto> update(@PathVariable UUID id, @RequestBody TournamentRequest request) {
        return ResponseEntity.ok(tournamentService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

