package com.devsync.devsync_server.workspace.controller;

import com.devsync.devsync_server.workspace.dto.DashboardResponse;
import com.devsync.devsync_server.workspace.dto.WorkspaceDashboardResponse;
import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.service.DashboardService;
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
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final TeamService teamService;

    @Value("${jwt.secret:YOUR_SUPER_SECRET_KEY_THAT_IS_AT_LEAST_256_BITS_LONG_FOR_HMAC}")
    private String jwtSecret;

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> loadDashboardLayout(
            @RequestHeader("Authorization") String tokenHeader) {

        String pureToken = tokenHeader.substring(7);

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(pureToken)
                .getBody();

        Long userId = claims.get("userId", Long.class);

        String extractionName = claims.get("firstName", String.class);

        if (extractionName == null || extractionName.isBlank()) {
            extractionName = claims.get("sub", String.class);
        }

        if (extractionName == null || extractionName.isBlank()) {
            extractionName = "User";
        }

        DashboardResponse summaryPayload =
                dashboardService.getDashboardSummary(
                        userId,
                        extractionName
                );

        return ResponseEntity.ok(summaryPayload);
    }

    @GetMapping("/workspace/{teamId}")
    public ResponseEntity<WorkspaceDashboardResponse> getWorkspaceDashboard(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long teamId
    ) {

        Long userId = validateAndGetUserId(tokenHeader);

        WorkspaceDashboardResponse response =
                dashboardService.getWorkspaceDashboard(
                        userId,
                        teamId
                );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/track-access")
    public ResponseEntity<Void> logDashboardActivity(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam Long entityId,
            @RequestParam String entityName,
            @RequestParam String entityType) {

        String pureToken = tokenHeader.substring(7);

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(pureToken)
                .getBody();

        Long userId = claims.get("userId", Long.class);

        dashboardService.trackUserAccess(
                userId,
                entityId,
                entityName,
                entityType
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create")
    public ResponseEntity<Team> createTeam(
            @RequestHeader("Authorization") String token,
            @RequestParam String name,
            @RequestParam boolean isPrivate) {

        Long userId = validateAndGetUserId(token);

        return ResponseEntity.ok(
                teamService.createTeam(
                        userId,
                        name,
                        isPrivate
                )
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