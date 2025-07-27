package com.example.definex.taskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatedDepartmentResponse {
    private String name;
    private List<ProjectSummaryResponse> projects;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
