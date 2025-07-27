package com.example.definex.taskmanagement.exception;

import com.example.definex.taskmanagement.entities.TaskStateType;

public class InvalidTaskStateTransitionException extends TaskStateException {
    public InvalidTaskStateTransitionException(TaskStateType currentState, TaskStateType newState) {
        super("Invalid state transition from " + currentState.toString() + " to " + newState.toString(), currentState, newState);
    }
}