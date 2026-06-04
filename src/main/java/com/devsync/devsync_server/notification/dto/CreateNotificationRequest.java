package com.devsync.devsync_server.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationRequest {
    private Long userId;
    private String title;
    private String message;
}