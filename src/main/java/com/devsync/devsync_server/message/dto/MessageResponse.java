package com.devsync.devsync_server.message.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {
    private Long id;
    private String content;
    private String type;
    private String codeLanguage;
    private Long senderId;
    private Long channelId;
    private Long recipientId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}