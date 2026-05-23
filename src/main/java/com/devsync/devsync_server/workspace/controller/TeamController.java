package com.devsync.devsync_server.workspace.controller;

import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.model.TeamMembership;
import com.devsync.devsync_server.workspace.service.TeamService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Value("${jwt.secret:YOUR_SUPER_SECRET_KEY_THAT_IS_AT_LEAST_256_BITS_LONG_FOR_HMAC}")
    private String jwtSecret;

    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(
            @RequestHeader("Authorization") String token,
            @RequestParam String name,
            @RequestParam boolean isPrivate) {

        Long userId = validateAndGetUserId(token);
        return ResponseEntity.ok(teamService.createTeam(userId, name, isPrivate));
    }

    @PostMapping("/join/{teamId}")
    public ResponseEntity<TeamMembership> joinTeam(
            @RequestHeader("Authorization") String token,
            @PathVariable Long teamId) {

        Long userId = validateAndGetUserId(token);
        return ResponseEntity.ok(teamService.joinTeam(userId, teamId));
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<TeamMembership> approveMember(
            @RequestHeader("Authorization") String token,
            @PathVariable Long requestId) {

        Long userId = validateAndGetUserId(token);
        return ResponseEntity.ok(teamService.approveMember(userId, requestId));
    }

    private Long validateAndGetUserId(String header) {
        String token = header.substring(7);

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.get("userId", Long.class);
    }
}