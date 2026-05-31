package com.devsync.devsync_server.task.service;

import com.devsync.devsync_server.task.dto.request.AssignTaskRequest;
import com.devsync.devsync_server.task.dto.request.CreateTaskRequest;
import com.devsync.devsync_server.task.dto.request.UpdateStatusRequest;
import com.devsync.devsync_server.task.dto.request.UpdateTaskRequest;
import com.devsync.devsync_server.task.dto.response.TaskActivityResponse;
import com.devsync.devsync_server.task.dto.response.TaskResponse;
import com.devsync.devsync_server.task.dto.response.TaskSummaryResponse;
import com.devsync.devsync_server.task.model.TaskStatus;

import java.util.List;

public interface TaskService {

    TaskResponse createTask(CreateTaskRequest request);

    TaskResponse getTaskById(Long taskId);

    List<TaskSummaryResponse> getTasksByTeam(Long teamId);

    List<TaskSummaryResponse> getTasksByAssignee(Long assigneeId);

    List<TaskSummaryResponse> getTasksByTeamAndStatus(Long teamId, TaskStatus status);

    TaskResponse updateTask(Long taskId, UpdateTaskRequest request);

    TaskResponse assignTask(Long taskId, AssignTaskRequest request);

    TaskResponse updateStatus(Long taskId, UpdateStatusRequest request);

    void deleteTask(Long taskId);

    List<TaskActivityResponse> getTaskActivities(Long taskId);
}
