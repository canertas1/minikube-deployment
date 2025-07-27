package com.example.definex.taskmanagement.dto.request;

import com.example.definex.taskmanagement.entities.ProjectStatusType;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateProjectRequest {
    private String title;
    private String description;
    private ProjectStatusType type;
}
