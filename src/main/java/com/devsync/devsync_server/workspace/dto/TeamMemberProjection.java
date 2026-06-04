package com.devsync.devsync_server.workspace.dto;

public record TeamMemberProjection(
        Long userId,
        String userName,
        String role,
        String status
) {
}