package com.devsync.devsync_server.message.controller;

import com.devsync.devsync_server.message.dto.MessageResponse;
import com.devsync.devsync_server.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageHttpController {

    private final MessageService messageService;

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageResponse>> getChannelHistory(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {

        log.info(
                "HTTP request received for channel history | channelId={} | page={} | size={}",
                channelId,
                page,
                size
        );

        try {

            Pageable pageable = PageRequest.of(page, size);

            List<MessageResponse> history =
                    messageService.getChannelHistory(
                            channelId,
                            pageable
                    );

            log.info(
                    "Returning {} channel messages | channelId={}",
                    history.size(),
                    channelId
            );

            return ResponseEntity.ok(history);

        } catch (Exception ex) {

            log.error(
                    "Error fetching channel history | channelId={}",
                    channelId,
                    ex
            );

            throw ex;
        }
    }

    @GetMapping("/direct")
    public ResponseEntity<List<MessageResponse>> getDirectMessageHistory(
            @RequestParam Long userA,
            @RequestParam Long userB,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {

        log.info(
                "HTTP request received for direct messages | userA={} | userB={} | page={} | size={}",
                userA,
                userB,
                page,
                size
        );

        try {

            Pageable pageable = PageRequest.of(page, size);

            List<MessageResponse> history =
                    messageService.getDirectMessageHistory(
                            userA,
                            userB,
                            pageable
                    );

            log.info(
                    "Returning {} direct messages between users {} and {}",
                    history.size(),
                    userA,
                    userB
            );

            return ResponseEntity.ok(history);

        } catch (Exception ex) {

            log.error(
                    "Error fetching direct message history | userA={} | userB={}",
                    userA,
                    userB,
                    ex
            );

            throw ex;
        }
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId
    ) {

        log.info(
                "HTTP request to mark message as read | messageId={} | userId={}",
                messageId,
                userId
        );

        try {

            messageService.markAsRead(messageId, userId);

            log.info(
                    "Message marked as read successfully | messageId={} | userId={}",
                    messageId,
                    userId
            );

            return ResponseEntity.noContent().build();

        } catch (Exception ex) {

            log.error(
                    "Error marking message as read | messageId={} | userId={}",
                    messageId,
                    userId,
                    ex
            );

            throw ex;
        }
    }
}