package com.devsync.devsync_server.workspace.dto;

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
    private List<WorkspaceSummary> workspaces;
    private List<AppShortcutResponse> userApps;
    private List<FrequentActivityResponse> frequentlyAccessed;
}