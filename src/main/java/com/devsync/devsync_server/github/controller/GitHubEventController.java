package com.devsync.devsync_server.github.controller;

import com.devsync.devsync_server.github.entity.GitHubEvent;
import com.devsync.devsync_server.github.repository.GitHubEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/github/events")
public class GitHubEventController {

    private final GitHubEventRepository gitHubEventRepository;

    @GetMapping
    public List<GitHubEvent> getAllEvents() {

        return gitHubEventRepository.findAll();
    }
}