package com.devsync.devsync_server.message.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    private String senderName;

    private String senderAvatar;

    private Long channelId;

    private Long recipientId;

    private Boolean edited;

    private Long replyToMessageId;

    private List<String> attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}