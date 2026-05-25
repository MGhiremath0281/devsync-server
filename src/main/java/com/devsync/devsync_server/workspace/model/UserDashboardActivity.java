package com.devsync.devsync_server.workspace.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_dashboard_activities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDashboardActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long entityId; // Channel ID or Chat ID

    @Column(nullable = false)
    private String entityName;

    @Column(nullable = false)
    private String entityType;

    @Column(nullable = false)
    private LocalDateTime lastAccessedAt;
}