package com.devsync.devsync_server.message.controller;

import com.devsync.devsync_server.message.dto.ChannelResponse;
import com.devsync.devsync_server.message.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(
                channelService.createChannel(teamId, name)
        );
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<ChannelResponse>> getChannels(
            @PathVariable Long teamId
    ) {
        return ResponseEntity.ok(
                channelService.getChannels(teamId)
        );
    }
}