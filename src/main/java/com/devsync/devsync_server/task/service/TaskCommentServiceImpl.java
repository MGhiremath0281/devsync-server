package com.devsync.devsync_server.task.service;

import com.devsync.devsync_server.task.dto.request.CreateCommentRequest;
import com.devsync.devsync_server.task.dto.response.TaskCommentResponse;
import com.devsync.devsync_server.task.exception.TaskNotFoundException;
import com.devsync.devsync_server.task.mapper.TaskMapper;
import com.devsync.devsync_server.task.model.Task;
import com.devsync.devsync_server.task.model.TaskActivity;
import com.devsync.devsync_server.task.model.TaskComment;
import com.devsync.devsync_server.task.repository.TaskActivityRepository;
import com.devsync.devsync_server.task.repository.TaskCommentRepository;
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
public class TaskCommentServiceImpl implements TaskCommentService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final TaskActivityRepository taskActivityRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskCommentResponse addComment(Long taskId, CreateCommentRequest request) {
        log.info("Attempting to add comment to task ID: {} by user ID: {}", taskId, request.getAuthorId());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    log.warn("Failed to add comment. Task with ID {} not found.", taskId);
                    return new TaskNotFoundException(taskId);
                });

        TaskComment comment = TaskComment.builder()
                .task(task)
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .build();

        comment = taskCommentRepository.save(comment);
        log.debug("Comment saved successfully with ID: {}", comment.getId());
        TaskActivity activity = TaskActivity.builder()
                .task(task)
                .actorId(request.getAuthorId())
                .action("COMMENT_ADDED")
                .newValue(request.getContent())
                .build();
        taskActivityRepository.save(activity);
        log.debug("Recorded activity log for comment addition on task ID: {}", taskId);

        return taskMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getCommentsByTask(Long taskId) {
        log.debug("Fetching comments for task ID: {}", taskId);
        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(taskMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info("Attempting to delete comment ID: {}", commentId);

        if (taskCommentRepository.existsById(commentId)) {
            taskCommentRepository.deleteById(commentId);
            log.info("Successfully deleted comment ID: {}", commentId);
        } else {
            log.warn("Execution skip: Comment ID {} did not exist or was already deleted.", commentId);
        }
    }
}