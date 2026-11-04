package com.example.Phygital_Core.service;
import com.example.Phygital_Core.dto.*;
import com.example.Phygital_Core.entity.Team;
import com.example.Phygital_Core.entity.TeamMember;
import com.example.Phygital_Core.exception.AppException;
import com.example.Phygital_Core.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public List<TeamDto> getAllTeams() {
        // Берем только неудаленные команды
        return teamRepository.findAllByIsDeletedFalse().stream().map(this::mapToDto).toList();
    }

    public TeamDto getTeamById(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Команда не найдена"));
        return mapToDto(team);
    }

    public TeamDto createTeam(TeamRequest request) {
        Team team = new Team();
        team.setName(request.getName());
        team.setDiscipline(request.getDiscipline());

        if (request.getMembers() != null) {
            List<TeamMember> members = request.getMembers().stream().map(m -> {
                TeamMember member = new TeamMember();
                member.setName(m.getName());
                member.setCaptain(m.isCaptain());
                member.setUserId(m.getUserId());
                member.setTeam(team);
                return member;
            }).toList();
            team.setMembers(members);
        }

        teamRepository.save(team);
        return mapToDto(team);
    }

    public TeamDto updateTeam(UUID id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Команда не найдена"));

        team.setName(request.getName());
        team.setDiscipline(request.getDiscipline());

        if (request.getMembers() != null) {
            team.getMembers().clear();
            List<TeamMember> members = request.getMembers().stream().map(m -> {
                TeamMember member = new TeamMember();
                member.setName(m.getName());
                member.setCaptain(m.isCaptain());
                member.setUserId(m.getUserId());
                member.setTeam(team);
                return member;
            }).toList();
            team.getMembers().addAll(members);
        }

        teamRepository.save(team);
        return mapToDto(team);
    }

    // МЯГКОЕ УДАЛЕНИЕ
    public void deleteTeam(UUID id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new AppException(404, "Команда не найдена"));
        team.setDeleted(true); // Помечаем как удаленную
        teamRepository.save(team);
    }

    private TeamDto mapToDto(Team team) {
        List<TeamMemberDto> memberDtos = team.getMembers().stream().map(m ->
                TeamMemberDto.builder()
                        .id(m.getUserId())
                        .name(m.getName())
                        .captain(m.isCaptain())
                        .build()
        ).toList();

        return TeamDto.builder()
                .id(team.getId())
                .name(team.getName())
                .discipline(team.getDiscipline())
                .members(memberDtos)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .build();
    }
}