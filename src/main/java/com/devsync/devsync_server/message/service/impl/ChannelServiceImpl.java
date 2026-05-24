package com.devsync.devsync_server.message.service.impl;

import com.devsync.devsync_server.message.channel.Channel;
import com.devsync.devsync_server.message.dto.ChannelResponse;
import com.devsync.devsync_server.message.repository.ChannelRepository;
import com.devsync.devsync_server.message.service.ChannelService;
import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final TeamRepository teamRepository;

    @Override
    public ChannelResponse createChannel(Long teamId, String name) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new EntityNotFoundException("Team not found")
                );

        Channel channel = Channel.builder()
                .name(name)
                .team(team)
                .build();

        Channel saved = channelRepository.save(channel);

        return mapToResponse(saved);
    }

    @Override
    public List<ChannelResponse> getChannels(Long teamId) {

        return channelRepository.findByTeamId(teamId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ChannelResponse mapToResponse(Channel channel) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .name(channel.getName())
                .teamId(channel.getTeam().getId())
                .teamName(channel.getTeam().getName())
                .build();
    }
}