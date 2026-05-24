package com.devsync.devsync_server.message.repository;

import com.devsync.devsync_server.message.channel.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {

    List<Channel> findByTeamId(Long teamId);
}