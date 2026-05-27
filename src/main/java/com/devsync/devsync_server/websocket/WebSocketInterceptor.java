package com.devsync.devsync_server.websocket;

import com.devsync.devsync_server.workspace.repository.TeamMembershipRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor {

    private final TeamMembershipRepository membershipRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor != null &&
                accessor.getCommand() != null) {

            StompCommand command =
                    accessor.getCommand();

            // ONLY validate during CONNECT
            if (StompCommand.CONNECT.equals(command)) {

                log.info(
                        "STOMP CONNECT frame received"
                );

                String authHeader =
                        accessor.getFirstNativeHeader(
                                "Authorization"
                        );

                if (authHeader == null ||
                        !authHeader.startsWith("Bearer ")) {

                    log.error(
                            "Access Denied: Missing JWT token during WebSocket CONNECT"
                    );

                    throw new SecurityException(
                            "Access Denied: Token missing."
                    );
                }

                String token =
                        authHeader.substring(7);

                try {

                    Key key =
                            Keys.hmacShaKeyFor(
                                    jwtSecret.getBytes(
                                            StandardCharsets.UTF_8
                                    )
                            );

                    Claims claims =
                            Jwts.parserBuilder()
                                    .setSigningKey(key)
                                    .build()
                                    .parseClaimsJws(token)
                                    .getBody();

                    Long userId =
                            claims.get(
                                    "userId",
                                    Long.class
                            );

                    // Store authenticated user in session
                    accessor.getSessionAttributes()
                            .put("userId", userId);

                    log.info(
                            "WebSocket authenticated successfully | userId={}",
                            userId
                    );

                } catch (Exception ex) {

                    log.error(
                            "JWT validation failed during WebSocket CONNECT",
                            ex
                    );

                    throw new SecurityException(
                            "Unauthorized WebSocket connection."
                    );
                }
            }

            // Optional debug logs
            if (StompCommand.SUBSCRIBE.equals(command)) {

                log.info(
                        "STOMP SUBSCRIBE received | destination={}",
                        accessor.getDestination()
                );
            }

            if (StompCommand.SEND.equals(command)) {

                log.info(
                        "STOMP SEND received | destination={}",
                        accessor.getDestination()
                );
            }
        }

        return message;
    }
}