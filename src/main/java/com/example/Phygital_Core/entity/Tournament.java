package com.example.Phygital_Core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tournaments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String publicNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String discipline;
    private String format;
    private String city;
    private String venue;

    @Column(columnDefinition = "TEXT")
    private String shortDescription;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startAt;


    private LocalDateTime endAt;


    private LocalDateTime registrationEndsAt;

    @ElementCollection
    @CollectionTable(name = "tournament_rules", joinColumns = @JoinColumn(name = "tournament_id"))
    @Column(name = "rule")
    @Builder.Default
    private List<String> rules = new ArrayList<>();

    private String imageName;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Column(nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}