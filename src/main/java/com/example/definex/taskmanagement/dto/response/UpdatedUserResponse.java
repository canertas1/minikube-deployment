package com.example.definex.taskmanagement.dto.response;

import com.example.definex.taskmanagement.entities.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class UpdatedUserResponse {
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
