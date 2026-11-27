package com.example.Phygital_Core.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class TournamentDto {
    private UUID id;
    private boolean isPartner;
    private String publicNumber;
    private String name;
    private String discipline;
    private String format;
    private String city;
    private String venue;
    private String shortDescription;
    private String description;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime registrationEndsAt;
    private List<String> rules;
    private String imageName;
    private String imageUrl;
}
