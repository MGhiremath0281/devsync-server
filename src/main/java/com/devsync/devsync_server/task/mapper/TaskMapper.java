package com.devsync.devsync_server.task.mapper;

import com.devsync.devsync_server.task.dto.response.TaskActivityResponse;
import com.devsync.devsync_server.task.dto.response.TaskCommentResponse;
import com.devsync.devsync_server.task.dto.response.TaskResponse;
import com.devsync.devsync_server.task.dto.response.TaskSummaryResponse;
import com.devsync.devsync_server.task.model.Task;
import com.devsync.devsync_server.task.model.TaskActivity;
import com.devsync.devsync_server.task.model.TaskComment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TaskMapper {

    public TaskResponse toTaskResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .teamId(task.getTeamId())
                .reporterId(task.getReporterId())
                .assigneeId(task.getAssigneeId())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .comments(task.getComments().stream()
                        .map(this::toCommentResponse)
                        .collect(Collectors.toList()))
                .activities(task.getActivities().stream()
                        .map(this::toActivityResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    public TaskSummaryResponse toTaskSummaryResponse(Task task) {
        return TaskSummaryResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .status(task.getStatus())
                .priority(task.getPriority())
                .assigneeId(task.getAssigneeId())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .build();
    }

    public TaskCommentResponse toCommentResponse(TaskComment comment) {
        return TaskCommentResponse.builder()
                .id(comment.getId())
                .taskId(comment.getTask().getId())
                .authorId(comment.getAuthorId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    public TaskActivityResponse toActivityResponse(TaskActivity activity) {
        return TaskActivityResponse.builder()
                .id(activity.getId())
                .taskId(activity.getTask().getId())
                .actorId(activity.getActorId())
                .action(activity.getAction())
                .oldValue(activity.getOldValue())
                .newValue(activity.getNewValue())
                .createdAt(activity.getCreatedAt())
                .build();
    }

    public List<TaskSummaryResponse> toTaskSummaryList(List<Task> tasks) {
        return tasks.stream().map(this::toTaskSummaryResponse).collect(Collectors.toList());
    }
}
