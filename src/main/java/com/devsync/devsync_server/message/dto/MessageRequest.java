package com.devsync.devsync_server.message.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    private String content;
    private String type;         // Evaluated against MessageType enum
    private String codeLanguage; // Populated only if type is CODE
    private Long senderId;
    private Long channelId;      // Populated if sending to a channel
    private Long recipientId;    // Populated if sending a 1:1 DM
}