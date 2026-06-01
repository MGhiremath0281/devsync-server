package com.devsync.devsync_server.task.repository;

import com.devsync.devsync_server.task.model.Task;
import com.devsync.devsync_server.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByTeamId(Long teamId);
    List<Task> findByAssigneeId(Long assigneeId);
    List<Task> findByTeamIdAndStatus(Long teamId, TaskStatus status);
    List<Task> findByReporterId(Long reporterId);
}
