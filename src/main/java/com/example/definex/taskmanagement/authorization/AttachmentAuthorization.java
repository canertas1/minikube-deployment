package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.entities.Attachment;
import com.example.definex.taskmanagement.entities.Task;

public interface AttachmentAuthorization {
    boolean userCanAttachFileToTask(Task task);
    boolean userCanDownloadAttachment(Attachment attachment);
    boolean userCanDeleteAttachment(Attachment attachment);
    boolean userCanViewTaskAttachments(Task task);

}
