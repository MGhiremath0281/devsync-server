package com.devsync.devsync_server.auth.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}