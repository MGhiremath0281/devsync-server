package com.devsync.devsync_server.workspace.dto;

import com.devsync.devsync_server.message.dto.ChannelResponse;
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
public class WorkspaceDashboardResponse {

    private Team workspace;

    private List<ChannelResponse> channels;

    private int totalChannels;

    private int totalMembers;

    private String formattedDate;
}