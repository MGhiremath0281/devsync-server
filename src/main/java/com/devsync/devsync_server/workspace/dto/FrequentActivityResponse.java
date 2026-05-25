package com.devsync.devsync_server.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FrequentActivityResponse {
    private Long id;
    private String title;
    private String description;
    private String entityType; // e.g., "CHAT_CHANNEL", "DIRECT_MESSAGE"
    private LocalDateTime lastAccessedAt;
}