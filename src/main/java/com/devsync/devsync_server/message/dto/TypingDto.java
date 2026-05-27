package com.devsync.devsync_server.message.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TypingDto {
    private Long channelId;
    private Long recipientId;
    private Long senderId;// For 1:1 DMs typing states
    private Long userId;
    private boolean isTyping;
}