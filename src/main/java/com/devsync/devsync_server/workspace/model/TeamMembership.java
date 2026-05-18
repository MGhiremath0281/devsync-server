package com.devsync.devsync_server.workspace.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "team_memberships")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(nullable = false)
    private String role; // "LEAD", "DEVELOPER"

    @Column(nullable = false)
    private String status; // "PENDING", "APPROVED"

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;
}