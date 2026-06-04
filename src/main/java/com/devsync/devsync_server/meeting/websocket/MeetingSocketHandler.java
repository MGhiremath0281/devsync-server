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

        Long userId = null;

        if (headerAccessor.getSessionAttributes() != null) {
            userId = (Long) headerAccessor
                    .getSessionAttributes()
                    .get("userId");
        }

        log.info(
                "CONNECT | sessionId={} | userId={}",
                headerAccessor.getSessionId(),
                userId
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

        Long userId = null;

        if (headerAccessor.getSessionAttributes() != null) {
            userId = (Long) headerAccessor
                    .getSessionAttributes()
                    .get("userId");
        }

        log.info(
                "DISCONNECT | sessionId={} | userId={}",
                headerAccessor.getSessionId(),
                userId
        );
    }
}