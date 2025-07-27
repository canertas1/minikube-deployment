package com.example.definex.taskmanagement.exception;

import com.example.definex.taskmanagement.entities.TaskStateType;

public class TaskAccessDeniedException extends TaskStateException {
    public TaskAccessDeniedException(TaskStateType currentState, TaskStateType newState) {
        super("You don't have permission to access this task", currentState, newState);
    }
}