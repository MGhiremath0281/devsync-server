package com.devsync.devsync_server.message.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private String type;
    private String codeLanguage;
    private Long senderId;
    private Long channelId;
    private Long recipientId;
    private Long replyToMessageId;
    private List<String> attachments;
}