package com.example.definex.taskmanagement.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateCommentRequest {
    @NotNull
    private String content;
    @NotNull
    private Long taskId;
    @NotNull
    private Long userId;
}
