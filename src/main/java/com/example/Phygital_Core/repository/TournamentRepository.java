package com.example.Phygital_Core.repository;

import com.example.Phygital_Core.entity.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TournamentRepository extends JpaRepository<Tournament, UUID> {
    List<Tournament> findAllByIsDeletedFalse();
}