package com.devsync.devsync_server.workspace.service;

import com.devsync.devsync_server.message.dto.ChannelResponse;
import com.devsync.devsync_server.message.service.ChannelService;
import com.devsync.devsync_server.workspace.dto.AppShortcutResponse;
import com.devsync.devsync_server.workspace.dto.DashboardResponse;
import com.devsync.devsync_server.workspace.dto.FrequentActivityResponse;
import com.devsync.devsync_server.workspace.dto.WorkspaceDashboardResponse;
import com.devsync.devsync_server.workspace.dto.WorkspaceSummary;
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

    private final TeamService                    teamService;
    private final ChannelService                 channelService;
    private final UserDashboardActivityRepository activityRepository;

    // ── Shared date formatter ─────────────────────────────────────────────────

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("EEEE dd MMMM", Locale.ENGLISH);

    private String today() {
        return LocalDateTime.now().format(DATE_FMT);
    }

    // ── getDashboardSummary ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public DashboardResponse getDashboardSummary(Long userId, String fallbackName) {

        List<Team> primaryTeams = teamService.getMyTeams(userId);
        if (primaryTeams == null) {
            primaryTeams = new ArrayList<>();
        }

        List<WorkspaceSummary> workspaceSummaries = primaryTeams.stream()
                .filter(t -> t != null)
                .map(t -> WorkspaceSummary.builder()
                        .id(t.getId())
                        .name(t.getName() != null ? t.getName() : "Unnamed Workspace")
                        .build())
                .collect(Collectors.toList());

        List<AppShortcutResponse> appShortcuts = new ArrayList<>();

        if (!primaryTeams.isEmpty() && primaryTeams.get(0) != null) {
            Long primaryTeamId = primaryTeams.get(0).getId();

            List<ChannelResponse> systemChannels = channelService.getChannels(primaryTeamId);

            if (systemChannels != null) {
                appShortcuts = systemChannels.stream()
                        .filter(channel -> channel != null)
                        .map(channel -> AppShortcutResponse.builder()
                                .id(channel.getId())
                                .name(channel.getName() != null ? channel.getName() : "Unnamed Channel")
                                .type("CHANNEL")
                                .targetUrl("/workspace/" + primaryTeamId
                                        + "/channel/" + channel.getId())
                                .build())
                        .collect(Collectors.toList());
            }
        }

        List<UserDashboardActivity> rawActivities = activityRepository
                .findByUserIdOrderByLastAccessedAtDesc(userId, PageRequest.of(0, 5));

        List<FrequentActivityResponse> frequentActivities = new ArrayList<>();

        if (rawActivities != null) {
            frequentActivities = rawActivities.stream()
                    .filter(act -> act != null)
                    .map(act -> {
                        String entityType = act.getEntityType() != null ? act.getEntityType() : "UNKNOWN";
                        String displayTitle = act.getEntityName() != null ? act.getEntityName() : "Untitled Item";

                        return FrequentActivityResponse.builder()
                                .id(act.getEntityId())
                                .title(displayTitle)
                                .description("Last viewed active tracking node inside " + entityType.toLowerCase())
                                .entityType(entityType)
                                .lastAccessedAt(act.getLastAccessedAt() != null ? act.getLastAccessedAt() : LocalDateTime.now())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        return DashboardResponse.builder()
                .userName(fallbackName != null ? fallbackName : "User")
                .formattedDate(today())
                .workspaces(workspaceSummaries)
                .userApps(appShortcuts)
                .frequentlyAccessed(frequentActivities)
                .build();
    }

    // ── getWorkspaceDashboard ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public WorkspaceDashboardResponse getWorkspaceDashboard(Long userId, Long teamId) {

        // 1. Load workspace
        Team workspace = teamService.getTeamById(teamId);
        if (workspace == null) {
            throw new RuntimeException("Workspace not found");
        }

        // 2. Validate membership
        if (!teamService.isUserMember(userId, teamId)) {
            throw new RuntimeException("You are not a member of this workspace");
        }

        // 3. Load channels
        List<ChannelResponse> channels = channelService.getChannels(teamId);
        if (channels == null) {
            channels = new ArrayList<>();
        }

        // Map Team → WorkspaceSummary safely before returning
        WorkspaceSummary workspaceSummary = WorkspaceSummary.builder()
                .id(workspace.getId())
                .name(workspace.getName() != null ? workspace.getName() : "Unnamed Workspace")
                .build();

        // 4. Build response with safe DTO
        return WorkspaceDashboardResponse.builder()
                .workspace(workspaceSummary)
                .channels(channels)
                .totalChannels(channels.size())
                .totalMembers((int) teamService.getTotalMembers(teamId)) // Works perfectly now!
                .formattedDate(today())
                .build();
    }

    @Transactional
    public void trackUserAccess(Long userId, Long entityId, String entityName, String entityType) {

        UserDashboardActivity activity = activityRepository
                .findByUserIdAndEntityIdAndEntityType(userId, entityId, entityType)
                .orElse(UserDashboardActivity.builder()
                        .userId(userId)
                        .entityId(entityId)
                        .entityType(entityType != null ? entityType : "UNKNOWN")
                        .build());

        activity.setEntityName(entityName != null ? entityName : "Untitled");
        activity.setLastAccessedAt(LocalDateTime.now());

        activityRepository.save(activity);
    }
}