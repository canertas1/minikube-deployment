package com.example.definex.taskmanagement.dto.request;

import com.example.definex.taskmanagement.entities.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UpdateUserRequest {
    private String name;
    private String email;
    private Long departmentId;
    private Role role;
}
