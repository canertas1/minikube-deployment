package com.example.definex.taskmanagement.dto.mapper;

import com.example.definex.taskmanagement.dto.request.CreateDepartmentRequest;
import com.example.definex.taskmanagement.dto.response.CreatedDepartmentResponse;
import com.example.definex.taskmanagement.dto.response.DepartmentResponse;
import com.example.definex.taskmanagement.dto.response.ProjectSummaryResponse;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    Department createDepartmentRequestToDepartment(CreateDepartmentRequest createDepartmentRequest);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "projectToProjectSummaryResponse")
    CreatedDepartmentResponse departmentToCreatedDepartmentResponse(Department department);

    @Mapping(source = "projects", target = "projects", qualifiedByName = "projectToProjectSummaryResponse")
    DepartmentResponse departmentToDepartmentResponse(Department department);

    @Named("projectToProjectSummaryResponse")
    default List<ProjectSummaryResponse> projectToProjectSummaryDto(List<Project> projects) {
        if (projects == null) {
            return null;
        }

        return projects.stream()
                .map(project -> {
                    ProjectSummaryResponse dto = new ProjectSummaryResponse();
                    dto.setId(project.getId());
                    dto.setTitle(project.getTitle());
                    dto.setDescription(project.getDescription());
                    return dto;
                })
                .toList();
    }
}