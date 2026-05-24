package com.devsync.devsync_server.github.dto;

public class IssueEventDTO {

    private String action;
    private String title;
    private String creator;
    private String repository;
    private String state;

    public IssueEventDTO() {
    }

    public IssueEventDTO(String action,
                         String title,
                         String creator,
                         String repository,
                         String state) {
        this.action = action;
        this.title = title;
        this.creator = creator;
        this.repository = repository;
        this.state = state;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}