package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.CreateUserRequest;
import com.example.definex.taskmanagement.dto.request.UpdateUserRequest;
import com.example.definex.taskmanagement.dto.response.CreatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    CreatedUserResponse save(CreateUserRequest createUserRequest);
    UpdatedUserResponse update(UpdateUserRequest updateUserRequest, Long userId);
    UserResponse getById(Long id);
    void deleteById(Long id);
    Page<UserResponse> findAll(Pageable pageable);
}
