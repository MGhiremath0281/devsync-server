package com.devsync.github.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/github")
@Slf4j
public class GitHubWebhookController {

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(
            @RequestHeader("X-GitHub-Event") String eventType,
            @RequestBody Map<String, Object> payload
    ) {

        log.info("Received GitHub Event: {}", eventType);

        switch (eventType) {

            case "pull_request":
                log.info("Pull Request Event Received");
                break;

            case "push":
                log.info("Push Event Received");
                break;

            case "issues":
                log.info("Issue Event Received");
                break;

            default:
                log.info("Unhandled Event: {}", eventType);
        }

        return ResponseEntity.ok("Webhook received successfully");
    }
}