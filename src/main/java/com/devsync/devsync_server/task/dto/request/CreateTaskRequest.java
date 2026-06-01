package com.devsync.devsync_server.task.dto.request;

import com.devsync.devsync_server.task.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotNull(message = "Reporter ID is required")
    private Long reporterId;

    private Long assigneeId;

    private TaskPriority priority;

    private LocalDate dueDate;
}
