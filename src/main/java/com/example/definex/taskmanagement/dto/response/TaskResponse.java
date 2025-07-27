package com.example.definex.taskmanagement.dto.response;

import com.example.definex.taskmanagement.entities.TaskPriorityType;
import com.example.definex.taskmanagement.entities.TaskStateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskResponse {
    private String title;
    private String userStoryDescription;
    private String acceptanceCriteria;
    private TaskStateType state;
    private TaskPriorityType priority;
    private Long projectId;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
