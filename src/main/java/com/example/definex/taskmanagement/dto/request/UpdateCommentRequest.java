package com.example.definex.taskmanagement.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateCommentRequest {
    private String content;
}
