package com.devsync.devsync_server.github.service;

import com.devsync.devsync_server.github.dto.GitHubEventDTO;
import com.devsync.devsync_server.github.dto.IssueEventDTO;
import com.devsync.devsync_server.github.dto.PullRequestEventDTO;
import com.devsync.devsync_server.github.entity.GitHubEvent;
import com.devsync.devsync_server.github.repository.GitHubEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
@Service
@Slf4j
public class GitHubWebhookService {

    private final SimpMessagingTemplate messagingTemplate;
    private final GitHubEventRepository gitHubEventRepository;

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

        String action =
                (String) payload.get("action");

        Map<String, Object> repository =
                (Map<String, Object>) payload.get("repository");

        Map<String, Object> pullRequest =
                (Map<String, Object>) payload.get("pull_request");

        Map<String, Object> user =
                (Map<String, Object>) pullRequest.get("user");

        Map<String, Object> base =
                (Map<String, Object>) pullRequest.get("base");

        String repoName =
                (String) repository.get("full_name");

        String prTitle =
                (String) pullRequest.get("title");

        String author =
                (String) user.get("login");

        String targetBranch =
                (String) base.get("ref");

        PullRequestEventDTO dto =
                new PullRequestEventDTO(
                        action,
                        prTitle,
                        author,
                        repoName,
                        targetBranch
                );

        GitHubEvent githubEvent =
                new GitHubEvent(
                        "pull_request",
                        repoName,
                        author,
                        targetBranch,
                        LocalDateTime.now()
                );

        gitHubEventRepository.save(githubEvent);

        log.info("======= PULL REQUEST EVENT =======");
        log.info("Action: {}", action);
        log.info("Repository: {}", repoName);
        log.info("PR Title: {}", prTitle);
        log.info("Author: {}", author);
        log.info("Target Branch: {}", targetBranch);
        log.info("==================================");

        messagingTemplate.convertAndSend(
                "/topic/github/pr",
                dto
        );
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

        GitHubEventDTO eventDTO =
                new GitHubEventDTO(
                        "push",
                        repoName,
                        branch,
                        pusherName,
                        commitCount
                );

        GitHubEvent githubEvent =
                new GitHubEvent(
                        "push",
                        repoName,
                        pusherName,
                        branch,
                        LocalDateTime.now()
                );

        gitHubEventRepository.save(githubEvent);

        log.info("========== PUSH EVENT ==========");
        log.info("Repository: {}", repoName);
        log.info("Branch: {}", branch);
        log.info("Pusher: {}", pusherName);
        log.info("Commit Count: {}", commitCount);
        log.info("================================");

        messagingTemplate.convertAndSend(
                "/topic/github",
                eventDTO
        );
    }

    private void handleIssueEvent(
            Map<String, Object> payload
    ) {

        String action =
                (String) payload.get("action");

        Map<String, Object> repository =
                (Map<String, Object>) payload.get("repository");

        Map<String, Object> issue =
                (Map<String, Object>) payload.get("issue");

        Map<String, Object> user =
                (Map<String, Object>) issue.get("user");

        String repoName =
                (String) repository.get("full_name");

        String issueTitle =
                (String) issue.get("title");

        String creator =
                (String) user.get("login");

        String state =
                (String) issue.get("state");

        IssueEventDTO dto =
                new IssueEventDTO(
                        action,
                        issueTitle,
                        creator,
                        repoName,
                        state
                );

        GitHubEvent githubEvent =
                new GitHubEvent(
                        "issues",
                        repoName,
                        creator,
                        state,
                        LocalDateTime.now()
                );

        gitHubEventRepository.save(githubEvent);

        log.info("========== ISSUE EVENT ==========");
        log.info("Action: {}", action);
        log.info("Repository: {}", repoName);
        log.info("Issue Title: {}", issueTitle);
        log.info("Creator: {}", creator);
        log.info("State: {}", state);
        log.info("=================================");

        messagingTemplate.convertAndSend(
                "/topic/github/issues",
                dto
        );
    }
}