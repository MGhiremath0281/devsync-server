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
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() != null) {
            StompCommand command = accessor.getCommand();

            // Only validate on explicit STOMP CONNECT and SUBSCRIBE commands
            if (StompCommand.CONNECT.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
                String authHeader = accessor.getFirstNativeHeader("Authorization");

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    log.error("Access Denied: Token validation credentials missing for command {}", command);
                    throw new SecurityException("Access Denied: Token validation credentials missing.");
                }

                String token = authHeader.substring(7);
                try {
                    Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
                    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
                    Long userId = claims.get("userId", Long.class);

                    if (StompCommand.SUBSCRIBE.equals(command)) {
                        String destination = accessor.getDestination();
                        if (destination != null && destination.startsWith("/topic/channel/")) {
                            Long parentTeamId = 1L; // Fallback or dynamic calculation mapping

                            boolean isApproved = membershipRepository.existsByUserIdAndTeamIdAndStatus(userId, parentTeamId, "APPROVED");
                            if (!isApproved) {
                                log.warn("Forbidden subscription attempt by user ID: {}", userId);
                                throw new SecurityException("Forbidden: Your multi-tenant membership state is not approved.");
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Unauthorized connection frame verification footprint drop: {}", e.getMessage());
                    throw new SecurityException("Unauthorized connection frame verification footprint drop.");
                }
            }
        }
        return message;
    }
}