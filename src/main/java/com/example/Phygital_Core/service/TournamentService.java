package com.example.Phygital_Core.service;


import com.example.Phygital_Core.dto.TournamentDto;
import com.example.Phygital_Core.dto.TournamentRequest;
import com.example.Phygital_Core.entity.Tournament;
import com.example.Phygital_Core.exception.AppException;
import com.example.Phygital_Core.repository.TournamentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;

    public List<TournamentDto> getAll() {
        // Берем только неудаленные турниры
        return tournamentRepository.findAllByIsDeletedFalse().stream().map(this::mapToDto).toList();
    }

    public TournamentDto getById(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Турнир не найден"));
        return mapToDto(tournament);
    }

    public TournamentDto create(TournamentRequest request) {
        Tournament tournament = new Tournament();
        updateEntityFromRequest(tournament, request);
        tournamentRepository.save(tournament);
        return mapToDto(tournament);
    }

    public TournamentDto update(UUID id, TournamentRequest request) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Турнир не найден"));
        updateEntityFromRequest(tournament, request);
        tournamentRepository.save(tournament);
        return mapToDto(tournament);
    }

    // МЯГКОЕ УДАЛЕНИЕ
    public void delete(UUID id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Турнир не найден"));
        tournament.setDeleted(true); // Помечаем как удаленный
        tournamentRepository.save(tournament);
    }

    private void updateEntityFromRequest(Tournament t, TournamentRequest r) {
        t.setPublicNumber(r.getPublicNumber());
        t.setName(r.getName());
        t.setDiscipline(r.getDiscipline());
        t.setFormat(r.getFormat());
        t.setCity(r.getCity());
        t.setVenue(r.getVenue());
        t.setShortDescription(r.getShortDescription());
        t.setDescription(r.getDescription());
        t.setStartAt(r.getStartAt());
        t.setEndAt(r.getEndAt()); // Теперь может быть null
        t.setRegistrationEndsAt(r.getRegistrationEndsAt()); // Теперь может быть null
        if (r.getRules() != null) {
            t.setRules(r.getRules());
        }
        t.setImageName(r.getImageName());
        t.setImageUrl(r.getImageUrl());
    }

    private TournamentDto mapToDto(Tournament t) {
        return TournamentDto.builder()
                .id(t.getId())
                .publicNumber(t.getPublicNumber())
                .name(t.getName())
                .discipline(t.getDiscipline())
                .format(t.getFormat())
                .city(t.getCity())
                .venue(t.getVenue())
                .shortDescription(t.getShortDescription())
                .description(t.getDescription())
                .startAt(t.getStartAt())
                .endAt(t.getEndAt())
                .registrationEndsAt(t.getRegistrationEndsAt())
                .rules(t.getRules())
                .imageName(t.getImageName())
                .imageUrl(t.getImageUrl())
                .build();
    }
}
