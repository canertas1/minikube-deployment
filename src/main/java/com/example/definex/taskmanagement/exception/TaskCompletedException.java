package com.example.definex.taskmanagement.exception;

import com.example.definex.taskmanagement.entities.TaskStateType;

public class TaskCompletedException extends TaskStateException {
    public TaskCompletedException(TaskStateType currentState, TaskStateType newState) {
        super("Completed tasks cannot be modified", currentState, newState);
    }
}