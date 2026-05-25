package com.devsync.devsync_server.workspace.dto;

import com.devsync.devsync_server.workspace.model.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private String userName;
    private String formattedDate;
    private List<Team> workspaces;
    private List<AppShortcutResponse> userApps;
    private List<FrequentActivityResponse> frequentlyAccessed;
}