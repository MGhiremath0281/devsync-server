package com.devsync.devsync_server.github.dto;

public class GitHubPushEventDTO {

    private String repositoryName;
    private String branch;
    private String pusher;
    private int commitCount;

    public GitHubPushEventDTO() {
    }

    public GitHubPushEventDTO(String repositoryName,
                              String branch,
                              String pusher,
                              int commitCount) {
        this.repositoryName = repositoryName;
        this.branch = branch;
        this.pusher = pusher;
        this.commitCount = commitCount;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
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