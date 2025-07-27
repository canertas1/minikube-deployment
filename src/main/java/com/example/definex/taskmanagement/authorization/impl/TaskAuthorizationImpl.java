package com.example.definex.taskmanagement.authorization.impl;

import com.example.definex.taskmanagement.authorization.TaskAuthorization;
import com.example.definex.taskmanagement.entities.*;
import com.example.definex.taskmanagement.exception.*;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TaskAuthorizationImpl implements TaskAuthorization {

    public void canCreateTask(Project project) {
        User user = (User) getCurrentAuthentication().getPrincipal();
        if (!(isTeamLeader(user) || isGroupManagerForDepartment(user, project.getDepartment()))) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_TASK.toString());
        }
    }

    public boolean canAccessTask(Task task) {
        User user = (User) getCurrentAuthentication().getPrincipal();

        if (isLeaderOrManagerForDepartment(user, task.getProject().getDepartment())) {
            return true;
        }

        return isAssigneeForTask(user, task);
    }

    public void validateTaskAccess(Task task) {
        if (!canAccessTask(task)) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_ACCESS_TO_TASK.toString());
        }
    }

    public boolean canManageTask(Task task) {
        User user = (User) getCurrentAuthentication().getPrincipal();
        return isLeaderOrManagerForDepartment(user, task.getProject().getDepartment());
    }

    public void validateTaskManagement(Task task) {
        if (!canManageTask(task)) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_MANAGE_TASK.toString());
        }
    }

    public void validateTaskStateChange(Task task, TaskStateType newState) {

        if (!canAccessTask(task)) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_ACCESS_TO_TASK.getMessage());
        }

        if (task.getState() == TaskStateType.COMPLETED) {
            throw new TaskCompletedException(task.getState(), newState);
        }

        if (!isValidStateTransition(task.getState(), newState)) {
            throw new InvalidTaskStateTransitionException(task.getState(), newState);
        }
    }
    private boolean isValidStateTransition(TaskStateType currentState, TaskStateType newState) {

        return switch (currentState) {

            case BACKLOG ->     newState == TaskStateType.IN_ANALYSIS ||
                                newState == TaskStateType.IN_PROGRESS ||
                                newState == TaskStateType.COMPLETED   ||
                                newState == TaskStateType.CANCELLED   ;

            case IN_ANALYSIS -> newState == TaskStateType.BACKLOG ||
                                newState == TaskStateType.IN_PROGRESS ||
                                newState == TaskStateType.BLOCKED ||
                                newState == TaskStateType.COMPLETED ||
                                newState == TaskStateType.CANCELLED;

            case IN_PROGRESS -> newState == TaskStateType.IN_ANALYSIS ||
                                newState == TaskStateType.COMPLETED ||
                                newState == TaskStateType.BLOCKED ||
                                newState == TaskStateType.CANCELLED;

            case BLOCKED ->     newState == TaskStateType.IN_ANALYSIS ||
                                newState == TaskStateType.IN_PROGRESS ||
                                newState == TaskStateType.BACKLOG  ||
                                newState == TaskStateType.CANCELLED;

            case CANCELLED ->   newState == TaskStateType.IN_ANALYSIS ||
                                newState == TaskStateType.IN_PROGRESS ||
                                newState == TaskStateType.BACKLOG  ;
            default -> false;
        };
    }

    public boolean canAssignTask(Task task) {
        User user = (User) getCurrentAuthentication().getPrincipal();
        return isLeaderOrManagerForDepartment(user, task.getProject().getDepartment());
    }

    public void validateTaskAssignment(Task task) {
        if (!canAssignTask(task)) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_ASSIGN_TASK.toString());
        }
    }

    public boolean canChangeTaskPriority(Task task) {
        User user = (User) getCurrentAuthentication().getPrincipal();
        return isLeaderOrManagerForDepartment(user, task.getProject().getDepartment());
    }

    public void validateTaskPriorityChange(Task task) {
        if (!canChangeTaskPriority(task)) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_TASK_PRIORITY.toString());
        }
    }

    public boolean canDeleteTask(Task task) {
        User user = (User) getCurrentAuthentication().getPrincipal();
        return isLeaderOrManagerForDepartment(user, task.getProject().getDepartment());
    }

    public void validateTaskDeletion(Task task) {
        if (!canDeleteTask(task)) {
            throw new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_DELETE_TASK.toString());
        }
    }

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isTeamLeader(User user) {
        return user.getRole() == Role.TEAM_LEADER;
    }

    private boolean isGroupManagerForDepartment(User user, Department department) {
        return user.getRole() == Role.GROUP_MANAGER &&
                user.getDepartment().getId().equals(department.getId());
    }

    private boolean isLeaderOrManagerForDepartment(User user, Department department) {
        return isTeamLeader(user) || isGroupManagerForDepartment(user, department);
    }

    private boolean isAssigneeForTask(User user, Task task) {
        return task.getAssignee() != null &&
                task.getAssignee().getId().equals(user.getId());
    }
}