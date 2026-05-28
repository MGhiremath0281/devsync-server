package com.devsync.devsync_server.meeting.controller;

import com.devsync.devsync_server.meeting.dto.CreateMeetingRequest;
import com.devsync.devsync_server.meeting.dto.MeetingResponse;
import com.devsync.devsync_server.meeting.service.MeetingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Slf4j
public class MeetingController {

    private final MeetingService meetingService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/create/{teamId}")
    public ResponseEntity<MeetingResponse> createMeeting(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long teamId,
            @RequestBody CreateMeetingRequest request
    ) {
        log.info("Received POST /api/meetings/create/{} - Channel ID: {}, Type: {}",
                teamId, request.getChannelId(), request.getType());

        Long userId = validateAndGetUserId(tokenHeader);
        log.debug("Authorized user ID [{}] for meeting creation", userId);

        MeetingResponse response = meetingService.createMeeting(
                userId,
                teamId,
                request.getChannelId(),
                request.getType()
        );

        log.info("Successfully processed meeting creation. Meeting ID: [{}]", response.getMeetingId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/join/{meetingId}")
    public ResponseEntity<MeetingResponse> joinMeeting(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId
    ) {
        log.info("Received POST /api/meetings/join/{}", meetingId);

        Long userId = validateAndGetUserId(tokenHeader);
        log.debug("Authorized user ID [{}] requesting to join meeting [{}]", userId, meetingId);

        MeetingResponse response = meetingService.joinMeeting(userId, meetingId);

        log.info("User [{}] successfully joined meeting [{}]", userId, meetingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/leave/{meetingId}")
    public ResponseEntity<Void> leaveMeeting(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId
    ) {
        log.info("Received POST /api/meetings/leave/{}", meetingId);

        Long userId = validateAndGetUserId(tokenHeader);
        log.debug("Authorized user ID [{}] requesting to leave meeting [{}]", userId, meetingId);

        meetingService.leaveMeeting(userId, meetingId);

        log.info("User [{}] successfully processed leave request for meeting [{}]", userId, meetingId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{meetingId}/mic")
    public ResponseEntity<MeetingResponse> toggleMic(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId,
            @RequestParam boolean enabled
    ) {
        log.info("Received POST /api/meetings/{}/mic?enabled={}", meetingId, enabled);

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response = meetingService.toggleMic(userId, meetingId, enabled);

        log.info("Mic state successfully toggled to [{}] for user [{}] in meeting [{}]", enabled, userId, meetingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{meetingId}/camera")
    public ResponseEntity<MeetingResponse> toggleCamera(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId,
            @RequestParam boolean enabled
    ) {
        log.info("Received POST /api/meetings/{}/camera?enabled={}", meetingId, enabled);

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response = meetingService.toggleCamera(userId, meetingId, enabled);

        log.info("Camera state successfully toggled to [{}] for user [{}] in meeting [{}]", enabled, userId, meetingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{meetingId}/screen-share")
    public ResponseEntity<MeetingResponse> toggleScreenShare(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId,
            @RequestParam boolean enabled
    ) {
        log.info("Received POST /api/meetings/{}/screen-share?enabled={}", meetingId, enabled);

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response = meetingService.toggleScreenShare(userId, meetingId, enabled);

        log.info("Screen share state successfully toggled to [{}] for user [{}] in meeting [{}]", enabled, userId, meetingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<MeetingResponse>> getOngoingMeetings() {
        log.debug("Received GET /api/meetings/ongoing");

        List<MeetingResponse> responses = meetingService.getOngoingMeetings();

        log.debug("Returning {} ongoing meeting(s)", responses.size());
        return ResponseEntity.ok(responses);
    }

    private Long validateAndGetUserId(String header) {
        log.trace("Validating Authorization token header");

        try {
            if (header == null || !header.startsWith("Bearer ")) {
                log.warn("Authentication failed: Malformed or missing Authorization header");
                throw new IllegalArgumentException("Invalid Authorization header format");
            }

            String token = header.substring(7);

            Key key = Keys.hmacShaKeyFor(
                    jwtSecret.getBytes(StandardCharsets.UTF_8)
            );

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Long userId = claims.get("userId", Long.class);
            log.trace("Token parsed successfully for user ID [{}]", userId);
            return userId;

        } catch (Exception e) {
            log.warn("JWT Verification failed: {}", e.getMessage());
            // Re-throwing exception to let Spring Boot's Global Exception Handler or Filter chain handle HTTP response status code
            throw e;
        }
    }
}