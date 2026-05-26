package com.devsync.devsync_server.meeting.controller;

import com.devsync.devsync_server.meeting.dto.CreateMeetingRequest;
import com.devsync.devsync_server.meeting.dto.MeetingResponse;
import com.devsync.devsync_server.meeting.service.MeetingService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
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

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response =
                meetingService.createMeeting(
                        userId,
                        teamId,
                        request.getChannelId(),
                        request.getType()
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/join/{meetingId}")
    public ResponseEntity<MeetingResponse> joinMeeting(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId
    ) {

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response =
                meetingService.joinMeeting(
                        userId,
                        meetingId
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/leave/{meetingId}")
    public ResponseEntity<Void> leaveMeeting(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId
    ) {

        Long userId = validateAndGetUserId(tokenHeader);

        meetingService.leaveMeeting(
                userId,
                meetingId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{meetingId}/mic")
    public ResponseEntity<MeetingResponse> toggleMic(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId,
            @RequestParam boolean enabled
    ) {

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response =
                meetingService.toggleMic(
                        userId,
                        meetingId,
                        enabled
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{meetingId}/camera")
    public ResponseEntity<MeetingResponse> toggleCamera(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId,
            @RequestParam boolean enabled
    ) {

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response =
                meetingService.toggleCamera(
                        userId,
                        meetingId,
                        enabled
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{meetingId}/screen-share")
    public ResponseEntity<MeetingResponse> toggleScreenShare(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long meetingId,
            @RequestParam boolean enabled
    ) {

        Long userId = validateAndGetUserId(tokenHeader);

        MeetingResponse response =
                meetingService.toggleScreenShare(
                        userId,
                        meetingId,
                        enabled
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/ongoing")
    public ResponseEntity<List<MeetingResponse>> getOngoingMeetings() {

        return ResponseEntity.ok(
                meetingService.getOngoingMeetings()
        );
    }

    private Long validateAndGetUserId(String header) {

        String token = header.substring(7);

        Key key = Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }
}