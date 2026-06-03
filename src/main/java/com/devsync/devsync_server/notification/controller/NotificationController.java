package com.devsync.devsync_server.notification.controller;

import com.devsync.devsync_server.notification.dto.NotificationResponse;
import com.devsync.devsync_server.notification.service.NotificationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamNotifications(@RequestParam("token") String token) {
        Long userId = validateAndGetUserId("Bearer " + token);

        SseEmitter emitter = new SseEmitter(600000L);
        emitters.put(userId, emitter);

        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError((e) -> emitters.remove(userId));
        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
        } catch (Exception e) {
            emitters.remove(userId);
        }

        return emitter;
    }

    public static void sendRealTimeNotification(Long userId, NotificationResponse notification) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("NEW_NOTIFICATION")
                        .data(notification));
            } catch (Exception e) {
                emitters.remove(userId);
            }
        }
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(@RequestHeader("Authorization") String token) {
        Long userId = validateAndGetUserId(token);
        return ResponseEntity.ok(notificationService.getNotifications(userId));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestHeader("Authorization") String token) {
        Long userId = validateAndGetUserId(token);
        return ResponseEntity.ok(notificationService.getUnreadCount(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Long userId = validateAndGetUserId(token);
        notificationService.markAsRead(userId, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@RequestHeader("Authorization") String token) {
        Long userId = validateAndGetUserId(token);
        notificationService.markAllAsRead(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Long userId = validateAndGetUserId(token);
        notificationService.deleteNotification(userId, id);
        return ResponseEntity.noContent().build();
    }

    private Long validateAndGetUserId(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }
        String token = header.substring(7).trim();
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }
}