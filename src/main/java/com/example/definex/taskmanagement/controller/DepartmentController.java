package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateDepartmentRequest;
import com.example.definex.taskmanagement.dto.response.CreatedDepartmentResponse;
import com.example.definex.taskmanagement.dto.response.DepartmentResponse;
import com.example.definex.taskmanagement.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @PostMapping
    public ResponseEntity<CreatedDepartmentResponse> createDepartment(@Valid @RequestBody CreateDepartmentRequest request) {
        return ResponseEntity.ok(departmentService.save(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }
    @GetMapping
    public ResponseEntity<Page<DepartmentResponse>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(departmentService.findAll(pageable));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}