package com.devsync.devsync_server.meeting.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class MeetingSocketHandler {

    @EventListener
    public void handleWebSocketConnectListener(
            SessionConnectEvent event
    ) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        String sessionId = headerAccessor.getSessionId();

        log.info(
                "Meeting websocket connected. SessionId={}",
                sessionId
        );
    }

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        String sessionId = headerAccessor.getSessionId();

        log.info(
                "Meeting websocket disconnected. SessionId={}",
                sessionId
        );
    }
}