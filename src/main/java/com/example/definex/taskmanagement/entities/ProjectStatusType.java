package com.example.definex.taskmanagement.entities;

public enum ProjectStatusType {
    IN_PROGRESS("IN_PROGRESS"), CANCELLED("CANCELLED"), COMPLETED("COMPLETED");
    private final String projectStatusType;
    ProjectStatusType(String type) {
        projectStatusType = type;
    }
    String getType() {
        return projectStatusType;
    }
}
