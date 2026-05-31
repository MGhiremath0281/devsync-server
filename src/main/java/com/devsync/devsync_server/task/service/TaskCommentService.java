package com.devsync.devsync_server.task.service;

import com.devsync.devsync_server.task.dto.request.CreateCommentRequest;
import com.devsync.devsync_server.task.dto.response.TaskCommentResponse;

import java.util.List;

public interface TaskCommentService {
    TaskCommentResponse addComment(Long taskId, CreateCommentRequest request);
    List<TaskCommentResponse> getCommentsByTask(Long taskId);
    void deleteComment(Long commentId);
}
