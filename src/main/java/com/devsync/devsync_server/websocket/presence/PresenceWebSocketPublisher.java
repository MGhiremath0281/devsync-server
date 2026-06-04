package com.devsync.devsync_server.websocket.presence;

import com.devsync.devsync_server.workspace.model.TeamMembership;
import com.devsync.devsync_server.workspace.repository.TeamMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PresenceWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;
    private final TeamMembershipRepository membershipRepository;

    public void broadcastPresence(
            Long userId,
            boolean online
    ) {

        List<TeamMembership> memberships =
                membershipRepository.findByUserIdAndStatus(
                        userId,
                        "APPROVED"
                );

        for (TeamMembership membership : memberships) {

            Long teamId =
                    membership.getTeamId();

            PresenceEvent event =
                    PresenceEvent.builder()
                            .userId(userId)
                            .teamId(teamId)
                            .online(online)
                            .build();

            messagingTemplate.convertAndSend(
                    "/topic/team/" + teamId + "/presence",
                    event
            );

            log.info(
                    "Presence broadcast sent | teamId={} | userId={} | online={}",
                    teamId,
                    userId,
                    online
            );
        }
    }
}