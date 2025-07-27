package com.example.definex.taskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FileAttachmentResponse {
    private String filePath;
    private String fileName;
    private Long userId;
    private Long taskId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
