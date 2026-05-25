package com.devsync.devsync_server.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppShortcutResponse {
    private Long id;
    private String name;
    private String type; // e.g., "CHANNEL", "INTEGRATION", "SETTING"
    private String targetUrl;
}