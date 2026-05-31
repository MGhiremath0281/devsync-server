package com.devsync.devsync_server.task.dto.response;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaskActivityResponse {
    private Long id;
    private Long taskId;
    private Long actorId;
    private String action;
    private String oldValue;
    private String newValue;
    private LocalDateTime createdAt;
}
