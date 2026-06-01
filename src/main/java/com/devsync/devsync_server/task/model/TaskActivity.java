package com.devsync.devsync_server.task.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_activities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskActivity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    /** e.g. "STATUS_CHANGED", "ASSIGNED", "COMMENT_ADDED" */
    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
