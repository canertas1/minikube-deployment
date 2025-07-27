package com.example.definex.taskmanagement.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreatedUserResponse {
    private String name;
    private String email;
    private String password;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private Boolean isDeleted;
}
