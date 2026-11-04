package com.example.Phygital_Core.repository;

import com.example.Phygital_Core.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    List<Team> findAllByIsDeletedFalse();
}
