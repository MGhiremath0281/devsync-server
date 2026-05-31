package com.devsync.devsync_server.task.dto.request;

import com.devsync.devsync_server.task.model.TaskPriority;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateTaskRequest {
    private String title;
    private String description;
    private TaskPriority priority;
    private LocalDate dueDate;
}
