package com.devsync.devsync_server.task.service;

import com.devsync.devsync_server.task.dto.request.AssignTaskRequest;
import com.devsync.devsync_server.task.dto.request.CreateTaskRequest;
import com.devsync.devsync_server.task.dto.request.UpdateStatusRequest;
import com.devsync.devsync_server.task.dto.request.UpdateTaskRequest;
import com.devsync.devsync_server.task.dto.response.TaskActivityResponse;
import com.devsync.devsync_server.task.dto.response.TaskResponse;
import com.devsync.devsync_server.task.dto.response.TaskSummaryResponse;
import com.devsync.devsync_server.task.exception.TaskNotFoundException;
import com.devsync.devsync_server.task.mapper.TaskMapper;
import com.devsync.devsync_server.task.model.Task;
import com.devsync.devsync_server.task.model.TaskActivity;
import com.devsync.devsync_server.task.model.TaskPriority;
import com.devsync.devsync_server.task.model.TaskStatus;
import com.devsync.devsync_server.task.repository.TaskActivityRepository;
import com.devsync.devsync_server.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskResponse createTask(CreateTaskRequest request) {
        log.info("Creating new task with title: '{}' for team ID: {}", request.getTitle(), request.getTeamId());

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teamId(request.getTeamId())
                .reporterId(request.getReporterId())
                .assigneeId(request.getAssigneeId())
                .priority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM)
                .status(TaskStatus.TODO)
                .dueDate(request.getDueDate())
                .build();

        task = taskRepository.save(task);
        logActivity(task, request.getReporterId(), "TASK_CREATED", null, task.getTitle());

        log.info("Successfully created task with ID: {}", task.getId());
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long taskId) {
        log.debug("Fetching task details for ID: {}", taskId);
        Task task = findTaskOrThrow(taskId);
        return taskMapper.toTaskResponse(task);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryResponse> getTasksByTeam(Long teamId) {
        log.debug("Fetching tasks for team ID: {}", teamId);
        return taskMapper.toTaskSummaryList(taskRepository.findByTeamId(teamId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryResponse> getTasksByAssignee(Long assigneeId) {
        log.debug("Fetching tasks for assignee ID: {}", assigneeId);
        return taskMapper.toTaskSummaryList(taskRepository.findByAssigneeId(assigneeId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskSummaryResponse> getTasksByTeamAndStatus(Long teamId, TaskStatus status) {
        log.debug("Fetching tasks for team ID: {} with status: {}", teamId, status);
        return taskMapper.toTaskSummaryList(taskRepository.findByTeamIdAndStatus(teamId, status));
    }

    @Override
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest request) {
        log.info("Updating fields for task ID: {}", taskId);
        Task task = findTaskOrThrow(taskId);

        if (request.getTitle() != null) task.setTitle(request.getTitle());
        if (request.getDescription() != null) task.setDescription(request.getDescription());
        if (request.getPriority() != null) task.setPriority(request.getPriority());
        if (request.getDueDate() != null) task.setDueDate(request.getDueDate());

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    @Override
    public TaskResponse assignTask(Long taskId, AssignTaskRequest request) {
        Task task = findTaskOrThrow(taskId);
        String oldAssignee = task.getAssigneeId() != null ? task.getAssigneeId().toString() : "unassigned";

        log.info("Assigning task ID: {} from {} to user ID: {}", taskId, oldAssignee, request.getAssigneeId());

        task.setAssigneeId(request.getAssigneeId());
        task = taskRepository.save(task);

        logActivity(task, request.getActorId(), "ASSIGNED", oldAssignee, request.getAssigneeId().toString());
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public TaskResponse updateStatus(Long taskId, UpdateStatusRequest request) {
        Task task = findTaskOrThrow(taskId);
        String oldStatus = task.getStatus().name();

        log.info("Changing status of task ID: {} from {} to {}", taskId, oldStatus, request.getStatus());

        task.setStatus(request.getStatus());
        task = taskRepository.save(task);

        logActivity(task, request.getActorId(), "STATUS_CHANGED", oldStatus, request.getStatus().name());
        return taskMapper.toTaskResponse(task);
    }

    @Override
    public void deleteTask(Long taskId) {
        log.info("Attempting to delete task ID: {}", taskId);
        Task task = findTaskOrThrow(taskId);
        taskRepository.delete(task);
        log.info("Successfully deleted task ID: {}", taskId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskActivityResponse> getTaskActivities(Long taskId) {
        log.debug("Fetching activity history for task ID: {}", taskId);
        findTaskOrThrow(taskId);
        return taskActivityRepository.findByTaskIdOrderByCreatedAtDesc(taskId)
                .stream()
                .map(taskMapper::toActivityResponse)
                .collect(Collectors.toList());
    }

    private Task findTaskOrThrow(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Task lookup failed! Task with ID {} not found.", taskId);
                    return new TaskNotFoundException(taskId);
                });
    }

    private void logActivity(Task task, Long actorId, String action, String oldValue, String newValue) {
        log.debug("Recording activity log -> Task ID: {}, Action: {}, Actor: {}", task.getId(), action, actorId);
        TaskActivity activity = TaskActivity.builder()
                .task(task)
                .actorId(actorId)
                .action(action)
                .oldValue(oldValue)
                .newValue(newValue)
                .build();
        taskActivityRepository.save(activity);
    }
}