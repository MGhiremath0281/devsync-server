package com.devsync.devsync_server.task.dto.response;

import com.devsync.devsync_server.task.model.TaskPriority;
import com.devsync.devsync_server.task.model.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private Long teamId;
    private Long reporterId;
    private Long assigneeId;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TaskCommentResponse> comments;
    private List<TaskActivityResponse> activities;
}
