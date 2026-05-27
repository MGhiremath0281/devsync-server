package com.devsync.devsync_server.message.controller;

import com.devsync.devsync_server.message.dto.ChannelResponse;
import com.devsync.devsync_server.message.service.ChannelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/create")
    public ResponseEntity<ChannelResponse> createChannel(
            @RequestParam Long teamId,
            @RequestParam String name
    ) {

        log.info(
                "Creating channel | teamId={} | channelName={}",
                teamId,
                name
        );

        try {

            ChannelResponse response =
                    channelService.createChannel(teamId, name);

            log.info(
                    "Channel created successfully | channelId={} | teamId={}",
                    response.getId(),
                    teamId
            );

            return ResponseEntity.ok(response);

        } catch (Exception ex) {

            log.error(
                    "Error creating channel | teamId={} | channelName={}",
                    teamId,
                    name,
                    ex
            );

            throw ex;
        }
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ChannelResponse>> getChannels(
            @PathVariable Long teamId
    ) {

        log.info(
                "Fetching channels for team | teamId={}",
                teamId
        );

        try {

            List<ChannelResponse> channels =
                    channelService.getChannels(teamId);

            log.info(
                    "Fetched {} channels for teamId={}",
                    channels.size(),
                    teamId
            );

            return ResponseEntity.ok(channels);

        } catch (Exception ex) {

            log.error(
                    "Error fetching channels | teamId={}",
                    teamId,
                    ex
            );

            throw ex;
        }
    }
}