package com.example.definex.taskmanagement.dto.request;

import com.example.definex.taskmanagement.entities.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Role role;
}