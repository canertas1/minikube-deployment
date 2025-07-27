package com.example.definex.taskmanagement.dto.response;

import com.example.definex.taskmanagement.entities.ProjectStatusType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UpdatedProjectResponse {
    private Long departmentId;
    private String title;
    private String description;
    private ProjectStatusType type;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
