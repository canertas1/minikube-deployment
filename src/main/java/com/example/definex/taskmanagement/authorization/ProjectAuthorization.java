package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.entities.Project;

public interface ProjectAuthorization {
    void userHasAuthorization(Project project);
}
