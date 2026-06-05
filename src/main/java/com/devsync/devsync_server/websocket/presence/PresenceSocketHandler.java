package com.devsync.devsync_server.websocket.presence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class PresenceSocketHandler {

    private final PresenceService presenceService;
    private final PresenceWebSocketPublisher presencePublisher;

    @EventListener
    public void handleWebSocketConnectListener(
            SessionConnectEvent event
    ) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        Long userId = null;

        if (accessor.getSessionAttributes() != null) {
            userId = (Long) accessor
                    .getSessionAttributes()
                    .get("userId");
        }

        if (userId == null) {
            return;
        }

        presenceService.userConnected(
                userId,
                accessor.getSessionId()
        );

        presencePublisher.broadcastPresence(
                userId,
                true
        );

        log.info(
                "Presence CONNECT | userId={} | sessionId={}",
                userId,
                accessor.getSessionId()
        );
    }

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(
                        event.getMessage()
                );

        Long userId = null;

        if (accessor.getSessionAttributes() != null) {
            userId = (Long) accessor
                    .getSessionAttributes()
                    .get("userId");
        }

        if (userId == null) {
            return;
        }

        presenceService.userDisconnected(
                userId,
                accessor.getSessionId()
        );

        presencePublisher.broadcastPresence(
                userId,
                false
        );

        log.info(
                "Presence DISCONNECT | userId={} | sessionId={}",
                userId,
                accessor.getSessionId()
        );
    }
}