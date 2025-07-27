package com.example.definex.taskmanagement.exception;

import com.example.definex.taskmanagement.entities.TaskStateType;

public class TaskStateException extends RuntimeException{
    private TaskStateType currentState;
    private TaskStateType newState;

    public TaskStateException(String message, TaskStateType currentState, TaskStateType newState) {
        super(message);
        this.currentState = currentState;
        this.newState = newState;
    }

    public TaskStateType getCurrentState() {
        return currentState;
    }

    public TaskStateType getNewState() {
        return newState;
    }
}
