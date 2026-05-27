package com.devsync.devsync_server.message.service.impl;

import com.devsync.devsync_server.message.channel.Channel;
import com.devsync.devsync_server.message.dto.ChannelResponse;
import com.devsync.devsync_server.message.repository.ChannelRepository;
import com.devsync.devsync_server.message.service.ChannelService;
import com.devsync.devsync_server.workspace.model.Team;
import com.devsync.devsync_server.workspace.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChannelServiceImpl implements ChannelService {

    private final ChannelRepository channelRepository;
    private final TeamRepository teamRepository;

    @Override
    public ChannelResponse createChannel(Long teamId, String name) {
        log.info("Attempting to create channel '{}' for team ID: {}", name, teamId);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("Failed to create channel: Team ID {} not found", teamId);
                    return new EntityNotFoundException("Team not found");
                });

        Channel channel = Channel.builder()
                .name(name)
                .team(team)
                .build();

        Channel saved = channelRepository.save(channel);
        log.info("Successfully created channel: {} with ID: {}", saved.getName(), saved.getId());

        return mapToResponse(saved);
    }

    @Override
    public List<ChannelResponse> getChannels(Long teamId) {
        log.info("Fetching all channels for team ID: {}", teamId);

        List<ChannelResponse> channels = channelRepository.findByTeamId(teamId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        log.info("Returning {} channels for team ID: {}", channels.size(), teamId);
        return channels;
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