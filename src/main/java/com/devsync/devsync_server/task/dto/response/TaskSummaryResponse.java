package com.devsync.devsync_server.task.dto.response;

import com.devsync.devsync_server.task.model.TaskPriority;
import com.devsync.devsync_server.task.model.TaskStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskSummaryResponse {
    private Long id;
    private String title;
    private TaskStatus status;
    private TaskPriority priority;
    private Long assigneeId;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}
