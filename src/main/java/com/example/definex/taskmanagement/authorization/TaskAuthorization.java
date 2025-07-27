package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.entities.Project;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.TaskStateType;

public interface TaskAuthorization {
    void canCreateTask(Project project);
    boolean canAccessTask(Task task);
    void validateTaskAccess(Task task);
    boolean canManageTask(Task task);
    void validateTaskManagement(Task task);
    void validateTaskStateChange(Task task, TaskStateType newState);
    boolean canAssignTask(Task task);
    void validateTaskAssignment(Task task);
    boolean canChangeTaskPriority(Task task);
    void validateTaskPriorityChange(Task task);
    boolean canDeleteTask(Task task);
    void validateTaskDeletion(Task task);

}
