package com.devsync.devsync_server.github.dto;

public class PullRequestEventDTO {

    private String action;
    private String title;
    private String author;
    private String repository;
    private String targetBranch;

    public PullRequestEventDTO() {
    }

    public PullRequestEventDTO(String action,
                               String title,
                               String author,
                               String repository,
                               String targetBranch) {
        this.action = action;
        this.title = title;
        this.author = author;
        this.repository = repository;
        this.targetBranch = targetBranch;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }
}