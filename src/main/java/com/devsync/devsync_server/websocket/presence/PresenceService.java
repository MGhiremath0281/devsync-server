package com.devsync.devsync_server.websocket.presence;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PresenceService {

    private final RedisTemplate<String, String> redisTemplate;

    private String buildKey(Long userId) {
        return "presence:user:" + userId;
    }

    public void userConnected(
            Long userId,
            String sessionId
    ) {

        String key = buildKey(userId);

        redisTemplate.opsForSet()
                .add(key, sessionId);

        log.info(
                "User connected | userId={} | sessionId={}",
                userId,
                sessionId
        );
    }

    public void userDisconnected(
            Long userId,
            String sessionId
    ) {

        String key = buildKey(userId);

        redisTemplate.opsForSet()
                .remove(key, sessionId);

        Long remaining =
                redisTemplate.opsForSet()
                        .size(key);

        if (remaining == null || remaining == 0) {
            redisTemplate.delete(key);
        }

        log.info(
                "User disconnected | userId={} | sessionId={}",
                userId,
                sessionId
        );
    }

    public boolean isOnline(Long userId) {

        String key = buildKey(userId);

        Long size =
                redisTemplate.opsForSet()
                        .size(key);

        return size != null && size > 0;
    }

    public Set<String> getActiveSessions(
            Long userId
    ) {

        return redisTemplate.opsForSet()
                .members(buildKey(userId));
    }

    public Set<Long> getOnlineUserIds() {

        Set<String> keys =
                redisTemplate.keys("presence:user:*");

        if (keys == null || keys.isEmpty()) {
            return Set.of();
        }

        return keys.stream()
                .map(key ->
                        Long.parseLong(
                                key.substring(
                                        "presence:user:".length()
                                )
                        )
                )
                .collect(Collectors.toSet());
    }
}