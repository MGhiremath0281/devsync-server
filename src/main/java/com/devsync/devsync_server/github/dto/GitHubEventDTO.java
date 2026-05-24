package com.devsync.devsync_server.github.dto;

public class GitHubEventDTO {

    private String type;
    private String repository;
    private String branch;
    private String pusher;
    private int commitCount;

    public GitHubEventDTO() {
    }

    public GitHubEventDTO(String type,
                          String repository,
                          String branch,
                          String pusher,
                          int commitCount) {
        this.type = type;
        this.repository = repository;
        this.branch = branch;
        this.pusher = pusher;
        this.commitCount = commitCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getPusher() {
        return pusher;
    }

    public void setPusher(String pusher) {
        this.pusher = pusher;
    }

    public int getCommitCount() {
        return commitCount;
    }

    public void setCommitCount(int commitCount) {
        this.commitCount = commitCount;
    }
}