package com.devsync.devsync_server.task.repository;

import com.devsync.devsync_server.task.model.TaskActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskActivityRepository extends JpaRepository<TaskActivity, Long> {
    List<TaskActivity> findByTaskIdOrderByCreatedAtDesc(Long taskId);
}
