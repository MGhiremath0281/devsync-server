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
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final TeamService      teamService;

    @Value("${jwt.secret:YOUR_SUPER_SECRET_KEY_THAT_IS_AT_LEAST_256_BITS_LONG_FOR_HMAC}")
    private String jwtSecret;

    // ── GET /summary ──────────────────────────────────────────────────────────

    @GetMapping("/summary")
    public ResponseEntity<DashboardResponse> loadDashboardLayout(
            @RequestHeader("Authorization") String tokenHeader) {

        Claims claims     = parseClaims(tokenHeader);
        Long   userId     = extractUserId(claims);
        String displayName = claims.get("firstName", String.class);

        if (displayName == null || displayName.isBlank()) displayName = claims.getSubject();
        if (displayName == null || displayName.isBlank()) displayName = "User";

        return ResponseEntity.ok(dashboardService.getDashboardSummary(userId, displayName));
    }

    // ── GET /workspace/{teamId} ───────────────────────────────────────────────

    @GetMapping("/workspace/{teamId}")
    public ResponseEntity<WorkspaceDashboardResponse> getWorkspaceDashboard(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long teamId) {

        Long userId = extractUserId(parseClaims(tokenHeader));
        return ResponseEntity.ok(dashboardService.getWorkspaceDashboard(userId, teamId));
    }

    // ── POST /track-access ────────────────────────────────────────────────────

    @PostMapping("/track-access")
    public ResponseEntity<Map<String, String>> logDashboardActivity(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam Long   entityId,
            @RequestParam String entityName,
            @RequestParam String entityType,
            @RequestParam(required = false, defaultValue = "") String description) {

        Long userId = extractUserId(parseClaims(tokenHeader));
        dashboardService.trackUserAccess(userId, entityId, entityName, entityType);

        Map<String, String> body = new HashMap<>();
        body.put("status",  "success");
        body.put("message", "Activity logged");
        return ResponseEntity.ok(body);
    }

    // ── POST /create ──────────────────────────────────────────────────────────

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createTeam(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestParam String  name,
            @RequestParam boolean isPrivate) {

        Long userId = extractUserId(parseClaims(tokenHeader));

        // ✅ KEY FIX: Do NOT return the raw Team JPA entity.
        //    Team has lazy-loaded relationships (members, channels, etc.).
        //    Jackson tries to serialize them, hits an uninitialized proxy,
        //    throws a LazyInitializationException, and Spring returns an
        //    HTML 500 error page — which is the "Unexpected token '<'" the
        //    frontend sees.
        //
        //    Instead, return a plain Map with only the fields the frontend
        //    actually needs (id + name). This is always safe to serialize.
        Team created = teamService.createTeam(userId, name, isPrivate);

        Map<String, Object> safeResponse = new HashMap<>();
        safeResponse.put("id",        created.getId());
        safeResponse.put("name",      created.getName());
        safeResponse.put("isPrivate", created.isPrivate());

        return ResponseEntity.ok(safeResponse);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Claims parseClaims(String authHeader) {
        String token = authHeader.startsWith("Bearer ")
                ? authHeader.substring(7) : authHeader;
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    private Long extractUserId(Claims claims) {
        Object raw = claims.get("userId");
        if (raw == null) throw new IllegalArgumentException(
                "JWT missing 'userId' claim — please log out and log in again.");
        return ((Number) raw).longValue();
    }
}