package com.devsync.devsync_server.task.dto.request;

import com.devsync.devsync_server.task.model.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdateStatusRequest {

    @NotNull(message = "Status is required")
    private TaskStatus status;

    @NotNull(message = "Actor ID is required")
    private Long actorId;
}
