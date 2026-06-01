package com.devsync.devsync_server.task.controller;

import com.devsync.devsync_server.task.dto.request.AssignTaskRequest;
import com.devsync.devsync_server.task.dto.request.CreateTaskRequest;
import com.devsync.devsync_server.task.dto.request.UpdateStatusRequest;
import com.devsync.devsync_server.task.dto.request.UpdateTaskRequest;
import com.devsync.devsync_server.task.dto.response.TaskActivityResponse;
import com.devsync.devsync_server.task.dto.response.TaskResponse;
import com.devsync.devsync_server.task.dto.response.TaskSummaryResponse;
import com.devsync.devsync_server.task.model.TaskStatus;
import com.devsync.devsync_server.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        log.info("REST request to create task: {}", request.getTitle()); // Assuming request has getTitle()
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long taskId) {
        log.info("REST request to fetch task ID: {}", taskId);
        return ResponseEntity.ok(taskService.getTaskById(taskId));
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TaskSummaryResponse>> getTasksByTeam(@PathVariable Long teamId) {
        log.info("REST request to fetch tasks for team ID: {}", teamId);
        return ResponseEntity.ok(taskService.getTasksByTeam(teamId));
    }

    @GetMapping("/team/{teamId}/status/{status}")
    public ResponseEntity<List<TaskSummaryResponse>> getTasksByTeamAndStatus(
            @PathVariable Long teamId,
            @PathVariable TaskStatus status) {
        log.info("REST request to fetch tasks for team ID: {} with status: {}", teamId, status);
        return ResponseEntity.ok(taskService.getTasksByTeamAndStatus(teamId, status));
    }

    @GetMapping("/assignee/{assigneeId}")
    public ResponseEntity<List<TaskSummaryResponse>> getTasksByAssignee(@PathVariable Long assigneeId) {
        log.info("REST request to fetch tasks for assignee ID: {}", assigneeId);
        return ResponseEntity.ok(taskService.getTasksByAssignee(assigneeId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskRequest request) {
        log.info("REST request to update task ID: {}", taskId);
        return ResponseEntity.ok(taskService.updateTask(taskId, request));
    }

    @PatchMapping("/{taskId}/assign")
    public ResponseEntity<TaskResponse> assignTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignTaskRequest request) {
        log.info("REST request to assign task ID: {} to user ID: {}", taskId, request.getAssigneeId()); // Assuming request has getAssigneeId()
        return ResponseEntity.ok(taskService.assignTask(taskId, request));
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update status of task ID: {} to {}", taskId, request.getStatus()); // Assuming request has getStatus()
        return ResponseEntity.ok(taskService.updateStatus(taskId, request));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        log.info("REST request to delete task ID: {}", taskId);
        taskService.deleteTask(taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/activities")
    public ResponseEntity<List<TaskActivityResponse>> getActivities(@PathVariable Long taskId) {
        log.info("REST request to fetch activities for task ID: {}", taskId);
        return ResponseEntity.ok(taskService.getTaskActivities(taskId));
    }
}