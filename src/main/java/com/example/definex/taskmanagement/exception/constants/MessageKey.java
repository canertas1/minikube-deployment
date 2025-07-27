package com.example.definex.taskmanagement.exception.constants;

public enum MessageKey {

    USER_NOT_FOUND_WITH_ID("User not found with given ID"),
    THIS_EMAIL_ALREADY_EXISTS("This email already exists"),
    INVALID_USERNAME_OR_PASSWORD("Invalid username or password"),

    DEPARTMENT_NAME_CANNOT_BE_EMPTY("Department name cannot be empty"),
    DEPARTMENT_NOT_FOUND_WITH_ID("Department not found with given ID"),

    PROJECT_NOT_FOUND_WITH_ID("Project not found with given ID"),
    PROJECT_TITLE_CANNOT_BE_EMPTY("Project title cannot be empty"),

    TASK_NOT_FOUND_WITH_ID("Task not found with given ID"),
    USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK("User is not assigned to this task"),
    REASON_IS_REQUIRED_FOR_CANCEL_OR_BLOCK_STATE("Reason is required for Cancelled or Blocked state"),
    USER_DOES_NOT_HAVE_ACCESS_TO_TASK("User does not have access to this task"),
    USER_DOES_NOT_HAVE_PERMISSION_TO_MANAGE_TASK("User does not have permission to manage this task"),
    USER_DOES_NOT_HAVE_PERMISSION_TO_ASSIGN_TASK("User does not have permission to assign this task"),
    USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_TASK_PRIORITY("User does not have permission to change task priority"),
    USER_DOES_NOT_HAVE_PERMISSION_TO_DELETE_TASK("User does not have permission to delete this task"),
    USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_TASK("User does not have create a task"),

    COMMENT_NOT_FOUND_WITH_ID("Comment not found with given ID"),

    ATTACHMENT_NOT_FOUND_WITH_ID("Attachment not found with given ID"),
    ATTACHMENT_NOT_FOUND_WITH_FILE_NAME("Attachment not found with given file name"),
    FILE_ACCESS_ERROR("File access error"),
    FILE_UPLOAD_ERROR("File upload error"),
    FILE_CANNOT_BE_EMPTY("File cannot be empty"),
    FILE_SIZE_EXCEEDS_LIMIT("File size cannot exceed 10 MB"),
    USER_CANNOT_ATTACH_FILES_TO_UNASSIGNED_TASKS("User cannot attach files to tasks not assigned to them"),
    USER_CANNOT_DOWNLOAD_FILES_FROM_UNASSIGNED_TASKS("User cannot download files from tasks not assigned to them"),
    USER_CANNOT_DELETE_FILES_FROM_UNASSIGNED_TASKS("User cannot delete files from tasks not assigned to them"),
    USER_CANNOT_VIEW_ATTACHMENTS_FROM_UNASSIGNED_TASKS("User cannot view attachments from tasks not assigned to them"),
    USER_NOT_FOUND_WITH_EMAIL("User cannot found with email"),


    USER_CANNOT_MANAGE_PROJECTS_IN_DEPARTMENT("User cannot manage projects in this department"),
    GROUP_MANAGER_CANNOT_COMMENT_ON_OTHER_DEPARTMENTS("Group manager cannot comment on other departments");

    private final String message;

    MessageKey(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}