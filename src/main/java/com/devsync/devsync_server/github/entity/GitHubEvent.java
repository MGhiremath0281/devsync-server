package com.devsync.devsync_server.github.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "github_events")
public class GitHubEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventType;

    private String repositoryName;

    private String actor;

    private String branchName;

    private LocalDateTime createdAt;

    public GitHubEvent() {
    }

    public GitHubEvent(String eventType,
                       String repositoryName,
                       String actor,
                       String branchName,
                       LocalDateTime createdAt) {
        this.eventType = eventType;
        this.repositoryName = repositoryName;
        this.actor = actor;
        this.branchName = branchName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}