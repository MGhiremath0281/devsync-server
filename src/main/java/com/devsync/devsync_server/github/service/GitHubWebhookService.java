package com.devsync.devsync_server.github.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class GitHubWebhookService {

    public void processEvent(String eventType,
                             Map<String, Object> payload) {

        switch (eventType) {

            case "pull_request":
                handlePullRequestEvent(payload);
                break;

            case "push":
                handlePushEvent(payload);
                break;

            case "issues":
                handleIssueEvent(payload);
                break;

            default:
                log.info("Unhandled Event: {}", eventType);
        }
    }

    private void handlePullRequestEvent(
            Map<String, Object> payload
    ) {

        log.info("Pull Request Event Received");
    }

    private void handlePushEvent(
            Map<String, Object> payload
    ) {

        Map<String, Object> repository =
                (Map<String, Object>) payload.get("repository");

        Map<String, Object> pusher =
                (Map<String, Object>) payload.get("pusher");

        String repoName =
                (String) repository.get("full_name");

        String pusherName =
                (String) pusher.get("name");

        String ref =
                (String) payload.get("ref");

        String branch =
                ref.replace("refs/heads/", "");

        Object[] commits =
                ((java.util.List<?>) payload.get("commits")).toArray();

        int commitCount = commits.length;

        log.info("========== PUSH EVENT ==========");
        log.info("Repository: {}", repoName);
        log.info("Branch: {}", branch);
        log.info("Pusher: {}", pusherName);
        log.info("Commit Count: {}", commitCount);
        log.info("================================");
    }

    private void handleIssueEvent(
            Map<String, Object> payload
    ) {

        log.info("Issue Event Received");
    }
}