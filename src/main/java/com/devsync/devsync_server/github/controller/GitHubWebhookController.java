package com.devsync.devsync_server.github.controller;

import com.devsync.devsync_server.github.service.GitHubWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/github")
@RequiredArgsConstructor
@Slf4j
public class GitHubWebhookController {

    private final GitHubWebhookService gitHubWebhookService;

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody Map<String, Object> payload
    ) {

        log.info("Received GitHub Event: {}", eventType);

        gitHubWebhookService.processEvent(eventType, payload);

        return ResponseEntity.ok("Webhook received successfully");
    }
}