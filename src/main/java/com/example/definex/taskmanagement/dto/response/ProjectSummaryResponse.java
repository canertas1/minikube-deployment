package com.example.definex.taskmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectSummaryResponse {
    private Long id;
    private String title;
    private String description;
}
