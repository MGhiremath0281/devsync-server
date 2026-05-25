package com.devsync.devsync_server.workspace.service;

import com.devsync.devsync_server.message.dto.ChannelResponse;
import com.devsync.devsync_server.message.service.ChannelService;
import com.devsync.devsync_server.workspace.dto.AppShortcutResponse;
import com.devsync.devsync_server.workspace.dto.DashboardResponse;
import com.devsync.devsync_server.workspace.dto.FrequentActivityResponse;
import com.devsync.devsync_server.workspace.dto.WorkspaceDashboardResponse;
import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.model.UserDashboardActivity;
import com.devsync.devsync_server.workspace.repository.UserDashboardActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TeamService teamService;
    private final ChannelService channelService;
    private final UserDashboardActivityRepository activityRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary(
            Long userId,
            String fallbackName
    ) {

        String formattedDate = LocalDateTime.now()
                .format(
                        DateTimeFormatter.ofPattern(
                                "EEEE dd MMMM",
                                Locale.ENGLISH
                        )
                );

        List<Team> primaryTeams =
                teamService.getMyTeams(userId);

        List<AppShortcutResponse> appShortcuts =
                new ArrayList<>();

        if (!primaryTeams.isEmpty()) {

            Long primaryTeamId =
                    primaryTeams.get(0).getId();

            List<ChannelResponse> systemChannels =
                    channelService.getChannels(primaryTeamId);

            appShortcuts = systemChannels.stream()
                    .map(channel ->
                            AppShortcutResponse.builder()
                                    .id(channel.getId())
                                    .name(channel.getName())
                                    .type("CHANNEL")
                                    .targetUrl(
                                            "/workspace/"
                                                    + primaryTeamId
                                                    + "/channel/"
                                                    + channel.getId()
                                    )
                                    .build()
                    )
                    .collect(Collectors.toList());
        }

        List<FrequentActivityResponse> frequentActivities =
                activityRepository
                        .findByUserIdOrderByLastAccessedAtDesc(
                                userId,
                                PageRequest.of(0, 5)
                        )
                        .stream()
                        .map(act ->
                                FrequentActivityResponse.builder()
                                        .id(act.getEntityId())
                                        .title(act.getEntityName())
                                        .description(
                                                "Last viewed active tracking node inside "
                                                        + act.getEntityType().toLowerCase()
                                        )
                                        .entityType(act.getEntityType())
                                        .lastAccessedAt(act.getLastAccessedAt())
                                        .build()
                        )
                        .collect(Collectors.toList());

        return DashboardResponse.builder()
                .userName(fallbackName)
                .formattedDate(formattedDate)
                .workspaces(primaryTeams)
                .userApps(appShortcuts)
                .frequentlyAccessed(frequentActivities)
                .build();
    }

    @Transactional(readOnly = true)
    public WorkspaceDashboardResponse getWorkspaceDashboard(
            Long userId,
            Long teamId
    ) {

        // 1. Load workspace
        Team workspace =
                teamService.getTeamById(teamId);

        // 2. Validate membership access
        boolean isMember =
                teamService.isUserMember(userId, teamId);

        if (!isMember) {

            throw new RuntimeException(
                    "You are not a member of this workspace"
            );
        }

        // 3. Load workspace channels
        List<ChannelResponse> channels =
                channelService.getChannels(teamId);

        // 4. Build workspace dashboard response
        return WorkspaceDashboardResponse.builder()
                .workspace(workspace)
                .channels(channels)
                .totalChannels(channels.size())
                .totalMembers(
                        (int) teamService.getTotalMembers(teamId)
                )
                .formattedDate(
                        LocalDateTime.now()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "EEEE dd MMMM",
                                                Locale.ENGLISH
                                        )
                                )
                )
                .build();
    }

    @Transactional
    public void trackUserAccess(
            Long userId,
            Long entityId,
            String entityName,
            String entityType
    ) {

        UserDashboardActivity activity =
                activityRepository
                        .findByUserIdAndEntityIdAndEntityType(
                                userId,
                                entityId,
                                entityType
                        )
                        .orElse(
                                UserDashboardActivity.builder()
                                        .userId(userId)
                                        .entityId(entityId)
                                        .entityType(entityType)
                                        .build()
                        );

        activity.setEntityName(entityName);
        activity.setLastAccessedAt(LocalDateTime.now());

        activityRepository.save(activity);
    }
}