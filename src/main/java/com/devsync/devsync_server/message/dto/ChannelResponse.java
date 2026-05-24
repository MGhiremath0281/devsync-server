package com.devsync.devsync_server.message.dto;

import lombok.*;

@Data
@Builder
public class ChannelResponse {
    private Long id;
    private String name;
    private Long teamId;
    private String teamName;
}