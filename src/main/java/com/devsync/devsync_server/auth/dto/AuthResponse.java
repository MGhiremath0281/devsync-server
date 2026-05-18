package com.devsync.devsync_server.auth.dto;

import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String email;
}