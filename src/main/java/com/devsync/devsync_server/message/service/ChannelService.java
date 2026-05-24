package com.devsync.devsync_server.message.service;

import com.devsync.devsync_server.message.dto.ChannelResponse;

import java.util.List;

public interface ChannelService {

    ChannelResponse createChannel(Long teamId, String name);

    List<ChannelResponse> getChannels(Long teamId);
}