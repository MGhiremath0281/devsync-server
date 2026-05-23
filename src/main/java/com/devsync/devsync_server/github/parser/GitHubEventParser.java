package com.devsync.devsync_server.github.parser;

import com.devsync.devsync_server.github.dto.GitHubPushEventDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class GitHubEventParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GitHubPushEventDTO parsePushEvent(String payload) {

        try {

            JsonNode root = objectMapper.readTree(payload);

            String repositoryName =
                    root.path("repository").path("full_name").asText();

            String branch =
                    root.path("ref").asText()
                            .replace("refs/heads/", "");

            String pusher =
                    root.path("pusher").path("name").asText();

            int commitCount =
                    root.path("commits").size();

            return new GitHubPushEventDTO(
                    repositoryName,
                    branch,
                    pusher,
                    commitCount
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GitHub push event", e);
        }
    }
}