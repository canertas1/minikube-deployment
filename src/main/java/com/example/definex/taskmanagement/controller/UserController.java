package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateUserRequest;
import com.example.definex.taskmanagement.dto.request.UpdateUserRequest;
import com.example.definex.taskmanagement.dto.response.CreatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UserResponse;
import com.example.definex.taskmanagement.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<CreatedUserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.ok(userService.save(request));
    }
    @PostMapping("/update/{userId}")
    public ResponseEntity<UpdatedUserResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest,@PathVariable Long userId){
        return ResponseEntity.ok(userService.update(updateUserRequest,userId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.findAll(pageable));
    }
}
