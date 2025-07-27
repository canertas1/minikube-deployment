package com.example.definex.taskmanagement.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UploadedFileAttachmentResponse {
    private String filePath;
    private String fileName;
    private Long userId;
    private Long taskId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
