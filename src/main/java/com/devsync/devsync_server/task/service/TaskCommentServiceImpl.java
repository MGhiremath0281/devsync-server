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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        TaskComment comment = TaskComment.builder()
                .task(task)
                .authorId(request.getAuthorId())
                .content(request.getContent())
                .build();

        comment = taskCommentRepository.save(comment);

        TaskActivity activity = TaskActivity.builder()
                .task(task)
                .actorId(request.getAuthorId())
                .action("COMMENT_ADDED")
                .newValue(request.getContent())
                .build();
        taskActivityRepository.save(activity);

        return taskMapper.toCommentResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getCommentsByTask(Long taskId) {
        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(taskMapper::toCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(Long commentId) {
        taskCommentRepository.deleteById(commentId);
    }
}
