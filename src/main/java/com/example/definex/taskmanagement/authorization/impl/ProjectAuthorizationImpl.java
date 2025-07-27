package com.example.definex.taskmanagement.authorization.impl;

import com.example.definex.taskmanagement.authorization.ProjectAuthorization;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.Project;
import com.example.definex.taskmanagement.entities.Role;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProjectAuthorizationImpl implements ProjectAuthorization {

    public void userHasAuthorization(Project project) {
        User user = (User) getCurrentAuthentication().getPrincipal();
        if (!isAuthorizedToManage(user, project.getDepartment())) {
            throw new UnauthorizedAccessException(MessageKey.USER_CANNOT_MANAGE_PROJECTS_IN_DEPARTMENT.toString() +project.getDepartment().getId());
        }
    }
    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    private boolean isAuthorizedToManage(User user, Department department) {
        return user.getRole() == Role.TEAM_LEADER ||
                (user.getRole() == Role.GROUP_MANAGER && user.getDepartment().getId().equals(department.getId()));
    }
}