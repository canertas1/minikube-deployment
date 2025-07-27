package com.example.definex.taskmanagement.authorization.impl;

import com.example.definex.taskmanagement.authorization.CommentAuthorization;
import com.example.definex.taskmanagement.entities.Role;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CommentAuthorizationImpl implements CommentAuthorization {

    public void userCanReachComment(Task task){
        User user = (User) getCurrentAuthentication().getPrincipal();
        if (user.getRole() == Role.TEAM_LEADER) {
            return;
        }

        if (user.getRole() == Role.GROUP_MANAGER && !task.getProject().getDepartment().getId().equals(user.getDepartment().getId())){
            throw new UnauthorizedAccessException(MessageKey.GROUP_MANAGER_CANNOT_COMMENT_ON_OTHER_DEPARTMENTS.toString());
        }

        if (!isAssigneeForTask(user,task)){
            throw new UnauthorizedAccessException(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString());
        }
    }

    private Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAssigneeForTask(User user, Task task) {
        return task.getAssignee() != null &&
                task.getAssignee().getId().equals(user.getId());
    }
}