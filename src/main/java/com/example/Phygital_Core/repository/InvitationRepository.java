package com.example.Phygital_Core.repository;

import com.example.Phygital_Core.entity.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findByRecipientId(UUID recipientId);
}
