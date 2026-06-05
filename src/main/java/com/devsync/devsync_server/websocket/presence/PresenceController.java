package com.devsync.devsync_server.websocket.presence;

import com.devsync.devsync_server.workspace.dto.TeamMemberDTO;
import com.devsync.devsync_server.workspace.dto.TeamMemberPresenceResponse;
import com.devsync.devsync_server.workspace.service.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/presence")
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;
    private final TeamService teamService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPresence(
            @PathVariable Long userId
    ) {

        boolean online =
                presenceService.isOnline(userId);

        return ResponseEntity.ok(
                Map.of(
                        "userId", userId,
                        "online", online
                )
        );
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamMemberPresenceResponse>>
    getTeamPresence(
            @PathVariable Long teamId
    ) {

        long totalStart = System.currentTimeMillis();

        long teamFetchStart = System.currentTimeMillis();

        List<TeamMemberDTO> members =
                teamService.getTeamMembersWithNames(teamId);

        log.info(
                "Team members fetch took {} ms",
                System.currentTimeMillis() - teamFetchStart
        );

        log.info(
                "Team contains {} members",
                members.size()
        );

        long redisStart = System.currentTimeMillis();

        Set<Long> onlineUsers =
                presenceService.getOnlineUserIds();

        log.info(
                "Redis fetch took {} ms",
                System.currentTimeMillis() - redisStart
        );

        long mappingStart = System.currentTimeMillis();

        List<TeamMemberPresenceResponse> response =
                members.stream()
                        .map(member ->
                                new TeamMemberPresenceResponse(
                                        member.userId(),
                                        member.userName(),
                                        member.role(),
                                        member.status(),
                                        onlineUsers.contains(
                                                member.userId()
                                        )
                                )
                        )
                        .toList();

        log.info(
                "DTO mapping took {} ms",
                System.currentTimeMillis() - mappingStart
        );

        log.info(
                "Total presence endpoint took {} ms",
                System.currentTimeMillis() - totalStart
        );

        return ResponseEntity.ok(response);
    }
}