package com.devsync.devsync_server.websocket.presence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PresenceEvent {

    private Long userId;

    private Long teamId;

    private boolean online;
}