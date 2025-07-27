package com.example.definex.taskmanagement.authorization.impl;

import com.example.definex.taskmanagement.authorization.AttachmentAuthorization;
import com.example.definex.taskmanagement.entities.Attachment;
import com.example.definex.taskmanagement.entities.Role;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class AttachmentAuthorizationImpl implements AttachmentAuthorization {
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    public boolean userCanAttachFileToTask(Task task) {
        User user = getCurrentUser();

        if (user.getRole() == Role.TEAM_LEADER) {
            return true;
        }

        if (user.getRole() == Role.GROUP_MANAGER) {
            return isSameDepartment(user, task);
        }

        if (user.getRole() == Role.TEAM_MEMBER) {
            if (!isTaskAssignedToUser(user, task)) {
                throw new UnauthorizedAccessException(MessageKey.USER_CANNOT_ATTACH_FILES_TO_UNASSIGNED_TASKS.toString());
            }
            return true;
        }
        return false;
    }

    public boolean userCanDownloadAttachment(Attachment attachment) {
        User user = getCurrentUser();
        Task task = attachment.getTask();

        if (user.getRole() == Role.TEAM_LEADER) {
            return true;
        }

        if (user.getRole() == Role.GROUP_MANAGER) {
            return isSameDepartment(user, task);
        }

        if (user.getRole() == Role.TEAM_MEMBER) {
            if (isTaskAssignedToUser(user, task)) {
                return true;
            }
            throw new UnauthorizedAccessException(MessageKey.USER_CANNOT_DOWNLOAD_FILES_FROM_UNASSIGNED_TASKS.toString());
        }

        return false;
    }

    public boolean userCanDeleteAttachment(Attachment attachment) {
        User user = getCurrentUser();
        Task task = attachment.getTask();

        if (user.getRole() == Role.TEAM_LEADER) {
            return true;
        }

        if (user.getRole() == Role.GROUP_MANAGER) {
            return isSameDepartment(user, task);
        }

        if (user.getRole() == Role.TEAM_MEMBER) {
            boolean isCreator = Objects.equals(attachment.getUser().getId(), user.getId());
            boolean isAssigned = isTaskAssignedToUser(user, task);

            if (!isAssigned) {
                throw new UnauthorizedAccessException(MessageKey.USER_CANNOT_DELETE_FILES_FROM_UNASSIGNED_TASKS.toString());
            }

            if (!isCreator) {
                throw new UnauthorizedAccessException(MessageKey.USER_CANNOT_DELETE_FILES_FROM_UNASSIGNED_TASKS.toString());
            }

            return true;
        }
        return false;
    }

    public boolean userCanViewTaskAttachments(Task task) {
        User user = getCurrentUser();

        if (user.getRole() == Role.TEAM_LEADER) {
            return true;
        }

        if (user.getRole() == Role.GROUP_MANAGER) {
            return isSameDepartment(user, task);
        }

        if (user.getRole() == Role.TEAM_MEMBER) {
            if (isTaskAssignedToUser(user, task)) {
                return true;
            }
            throw new UnauthorizedAccessException(MessageKey.USER_CANNOT_VIEW_ATTACHMENTS_FROM_UNASSIGNED_TASKS.toString());
        }

        return false;
    }

    private boolean isSameDepartment(User user, Task task) {
        if (user == null || task == null || task.getProject() == null ||
                task.getProject().getDepartment() == null || user.getDepartment() == null) {
            return false;
        }
        return Objects.equals(user.getDepartment().getId(), task.getProject().getDepartment().getId());
    }

    private boolean isTaskAssignedToUser(User user, Task task) {
        if (user == null || task == null || task.getAssignee() == null) {
            return false;
        }
        return Objects.equals(task.getAssignee().getId(), user.getId());
    }
}