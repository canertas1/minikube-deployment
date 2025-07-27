package com.example.definex.taskmanagement.entities;

public enum TaskPriorityType {
    CRITICAL("CRITICAL"), HIGH("HIGH"), MEDIUM("MEDIUM"), LOW("LOW");
    private final String taskPriorityType;
    TaskPriorityType(String type) {
        taskPriorityType = type;
    }
    String getType() {
        return taskPriorityType;
    }
}
