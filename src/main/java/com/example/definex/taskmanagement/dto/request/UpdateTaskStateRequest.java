package com.example.definex.taskmanagement.dto.request;

import com.example.definex.taskmanagement.entities.TaskStateType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateTaskStateRequest {
    private TaskStateType newState;
    private String reason;
}
