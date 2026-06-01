package com.devsync.devsync_server.task.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long taskId) {
        super("Task not found with id: " + taskId);
    }
}
