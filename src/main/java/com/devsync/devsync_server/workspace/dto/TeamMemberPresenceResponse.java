package com.devsync.devsync_server.workspace.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamMemberPresenceResponse {

    private Long userId;

    private String username;

    private String role;

    private String status;

    private boolean online;
}
