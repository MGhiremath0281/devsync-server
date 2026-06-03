package com.devsync.devsync_server.workspace.controller;

import com.devsync.devsync_server.workspace.dto.TeamMemberDTO;
import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.model.TeamMembership;
import com.devsync.devsync_server.workspace.service.TeamService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<TeamMemberDTO>> getTeamMembers(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long teamId) {

        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.emptyList());
        }

        try {
            validateAndGetUserId(token);
            return ResponseEntity.ok(teamService.getTeamMembersWithNames(teamId));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.emptyList());
        }
    }

    @PostMapping("/{teamId}/members")
    public ResponseEntity<?> addMember(
            @RequestHeader("Authorization") String token,
            @PathVariable Long teamId,
            @RequestBody AddMemberRequest request) {
        try {
            Long requesterId = validateAndGetUserId(token);
            TeamMembership membership = teamService.addMember(requesterId, teamId, request.getEmail());
            return ResponseEntity.ok(membership);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}/members/{memberUserId}")
    public ResponseEntity<Void> removeMember(
            @RequestHeader("Authorization") String token,
            @PathVariable Long teamId,
            @PathVariable Long memberUserId) {
        try {
            Long requesterId = validateAndGetUserId(token);
            teamService.removeMember(requesterId, teamId, memberUserId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(
            @RequestHeader("Authorization") String token,
            @RequestParam String name,
            @RequestParam boolean isPrivate) {
        return ResponseEntity.ok(teamService.createTeam(validateAndGetUserId(token), name, isPrivate));
    }

    @PostMapping("/join/{teamId}")
    public ResponseEntity<TeamMembership> joinTeam(
            @RequestHeader("Authorization") String token,
            @PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.joinTeam(validateAndGetUserId(token), teamId));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyTeams(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(teamService.getMyTeams(validateAndGetUserId(token)));
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

    @Data
    public static class AddMemberRequest {
        private String email;
    }
}