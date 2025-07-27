package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.CreateProjectRequest;
import com.example.definex.taskmanagement.dto.request.UpdateProjectRequest;
import com.example.definex.taskmanagement.dto.response.CreatedProjectResponse;
import com.example.definex.taskmanagement.dto.response.ProjectResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedProjectResponse;

public interface ProjectService {
    CreatedProjectResponse save(CreateProjectRequest createProjectRequest,Long departmentId);
    UpdatedProjectResponse update(UpdateProjectRequest updateProjectRequest, Long projectId);
    ProjectResponse findById(Long id);
    void deleteById(Long id);
}
