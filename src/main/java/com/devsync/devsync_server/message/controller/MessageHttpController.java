package com.devsync.devsync_server.message.controller;


import com.devsync.devsync_server.message.dto.MessageResponse;
import com.devsync.devsync_server.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageHttpController {

    private final MessageService messageService;

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageResponse>> getChannelHistory(
            @PathVariable Long channelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<MessageResponse> history = messageService.getChannelHistory(channelId, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/direct")
    public ResponseEntity<List<MessageResponse>> getDirectMessageHistory(
            @RequestParam Long userA,
            @RequestParam Long userB,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<MessageResponse> history = messageService.getDirectMessageHistory(userA, userB, pageable);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/{messageId}/read")
    public ResponseEntity<Void> markMessageAsRead(
            @PathVariable Long messageId,
            @RequestParam Long userId) {

        messageService.markAsRead(messageId, userId);
        return ResponseEntity.noContent().build();
    }
}