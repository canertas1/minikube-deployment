package com.example.definex.taskmanagement.entities;

public enum TaskStateType {
    BACKLOG("BACKLOG"),
    IN_ANALYSIS("IN_ANALYSIS"),
    IN_PROGRESS("IN_PROGRESS"),
    COMPLETED("COMPLETED"),
    CANCELLED("CANCELLED"),
    BLOCKED("BLOCKED");

    private final String taskStateType;
    TaskStateType(String type) {
        taskStateType = type;
    }
    String getType() {
        return taskStateType;
    }
}
