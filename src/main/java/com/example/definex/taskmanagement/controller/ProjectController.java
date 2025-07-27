package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateProjectRequest;
import com.example.definex.taskmanagement.dto.request.UpdateProjectRequest;
import com.example.definex.taskmanagement.dto.response.CreatedProjectResponse;
import com.example.definex.taskmanagement.dto.response.ProjectResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedProjectResponse;
import com.example.definex.taskmanagement.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @PostMapping("/{departmentId}")
    public ResponseEntity<CreatedProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request,@PathVariable Long departmentId) {
        return ResponseEntity.ok(projectService.save(request,departmentId));
    }
    @PostMapping("/update/{projectId}")
    public ResponseEntity<UpdatedProjectResponse> updateProject(@RequestBody UpdateProjectRequest updateProjectRequest,@PathVariable Long projectId){
        return ResponseEntity.ok(projectService.update(updateProjectRequest,projectId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.findById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
