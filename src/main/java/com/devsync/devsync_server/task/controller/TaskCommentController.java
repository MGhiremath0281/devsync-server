package com.devsync.devsync_server.task.controller;

import com.devsync.devsync_server.task.dto.request.CreateCommentRequest;
import com.devsync.devsync_server.task.dto.response.TaskCommentResponse;
import com.devsync.devsync_server.task.service.TaskCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    @PostMapping("/{taskId}/comments")
    public ResponseEntity<TaskCommentResponse> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskCommentService.addComment(taskId, request));
    }

    @GetMapping("/{taskId}/comments")
    public ResponseEntity<List<TaskCommentResponse>> getComments(@PathVariable Long taskId) {
        return ResponseEntity.ok(taskCommentService.getCommentsByTask(taskId));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        taskCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
