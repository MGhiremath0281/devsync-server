package com.devsync.devsync_server.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketInterceptor webSocketInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint 1: Raw direct connection line for standalone tools (WebSocket King / Postman)
        registry.addEndpoint("/ws-raw")
                .setAllowedOriginPatterns("*");

        // Endpoint 2: Existing production connection line with SockJS browser fallbacks
        registry.addEndpoint("/ws-provider")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Subscriptions destination targets
        registry.enableSimpleBroker("/topic", "/queue");

        // Incoming execution route target prefix
        registry.setApplicationDestinationPrefixes("/app");

        // Target prefix for routing direct user-to-user DMs
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Attaches our security handshake interceptor pipeline
        registration.interceptors(webSocketInterceptor);
    }
}