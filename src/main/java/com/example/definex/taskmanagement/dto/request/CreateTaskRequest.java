package com.example.definex.taskmanagement.dto.request;

import com.example.definex.taskmanagement.entities.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTaskRequest {
    @NotNull
    private String title;
    private String userStoryDescription;
    private String acceptanceCriteria;
    private TaskStateType state;
    private TaskPriorityType priority;
}
