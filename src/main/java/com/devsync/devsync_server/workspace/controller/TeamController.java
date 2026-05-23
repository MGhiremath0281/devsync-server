package com.devsync.devsync_server.workspace.controller;

import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.model.TeamMembership;
import com.devsync.devsync_server.workspace.repository.TeamMembershipRepository;
import com.devsync.devsync_server.workspace.repository.TeamRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamRepository teamRepository;
    private final TeamMembershipRepository membershipRepository;

    @Value("${jwt.secret:YOUR_SUPER_SECRET_KEY_THAT_IS_AT_LEAST_256_BITS_LONG_FOR_HMAC}")
    private String jwtSecret;

    @PostMapping("/create")
    public ResponseEntity<?> createTeam(@RequestHeader("Authorization") String tokenHeader, @RequestParam String name, @RequestParam boolean isPrivate) {
        Long userId = validateAndGetUserId(tokenHeader);

        if (teamRepository.findByName(name).isPresent()) {
            return ResponseEntity.badRequest().body("Workspace name already claimed!");
        }

        Team team = Team.builder()
                .name(name)
                .ownerId(userId)
                .isPrivate(isPrivate)
                .build();
        Team savedTeam = teamRepository.save(team);

        TeamMembership membership = TeamMembership.builder()
                .userId(userId)
                .teamId(savedTeam.getId())
                .role("LEAD")
                .status("APPROVED")
                .joinedAt(LocalDateTime.now())
                .build();
        membershipRepository.save(membership);

        return ResponseEntity.ok(savedTeam);
    }

    @PostMapping("/join/{teamId}")
    public ResponseEntity<?> joinTeam(@RequestHeader("Authorization") String tokenHeader, @PathVariable Long teamId) {
        Long userId = validateAndGetUserId(tokenHeader);
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new RuntimeException("Workspace doesn't exist"));

        if (membershipRepository.findByUserIdAndTeamId(userId, teamId).isPresent()) {
            return ResponseEntity.badRequest().body("Active profile link mapping path already occupied.");
        }

        String status = team.isPrivate() ? "PENDING" : "APPROVED";
        TeamMembership membership = TeamMembership.builder()
                .userId(userId)
                .teamId(teamId)
                .role("DEVELOPER")
                .status(status)
                .joinedAt(status.equals("APPROVED") ? LocalDateTime.now() : null)
                .build();

        return ResponseEntity.ok(membershipRepository.save(membership));
    }

    @PostMapping("/approve/{requestId}")
    public ResponseEntity<?> approveMember(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long requestId) {

        Long userId = validateAndGetUserId(tokenHeader);

        TeamMembership membership = membershipRepository.findById(requestId)
                .orElse(null);

        if (membership == null) {
            return ResponseEntity.badRequest().body("Invalid membership requestId");
        }

        Team team = teamRepository.findById(membership.getTeamId())
                .orElse(null);

        if (team == null) {
            return ResponseEntity.badRequest().body("Team does not exist for this membership");
        }

        if (!team.getOwnerId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only the Team Lead can execute approvals.");
        }

        membership.setStatus("APPROVED");
        membership.setJoinedAt(LocalDateTime.now());

        return ResponseEntity.ok(membershipRepository.save(membership));
    }

    private Long validateAndGetUserId(String header) {
        String token = header.substring(7);
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.get("userId", Long.class);
    }
}