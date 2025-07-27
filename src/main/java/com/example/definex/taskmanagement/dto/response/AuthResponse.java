package com.example.definex.taskmanagement.dto.response;

import com.example.definex.taskmanagement.entities.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthResponse {
    private String token;
    private String username;
    private Role role;

    public AuthResponse(String token, String username, Role role) {
        this.token = token;
        this.username = username;
        this.role = role;
    }


}