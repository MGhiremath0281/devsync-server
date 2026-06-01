package com.devsync.devsync_server.task.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AssignTaskRequest {

    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;

    @NotNull(message = "Actor ID is required")
    private Long actorId;
}
