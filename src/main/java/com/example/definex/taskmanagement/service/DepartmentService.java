package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.CreateDepartmentRequest;
import com.example.definex.taskmanagement.dto.response.CreatedDepartmentResponse;
import com.example.definex.taskmanagement.dto.response.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    CreatedDepartmentResponse save(CreateDepartmentRequest createDepartmentRequest);
    DepartmentResponse findById(Long id);
    Page<DepartmentResponse> findAll(Pageable pageable);
    void deleteById(Long id);
}
