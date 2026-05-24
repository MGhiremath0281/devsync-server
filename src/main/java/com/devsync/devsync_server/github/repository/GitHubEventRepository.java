package com.devsync.devsync_server.github.repository;

import com.devsync.devsync_server.github.entity.GitHubEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GitHubEventRepository
        extends JpaRepository<GitHubEvent, Long> {

}