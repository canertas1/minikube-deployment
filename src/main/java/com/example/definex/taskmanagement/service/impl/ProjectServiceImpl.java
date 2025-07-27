package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.authorization.ProjectAuthorization;
import com.example.definex.taskmanagement.dto.mapper.ProjectMapper;
import com.example.definex.taskmanagement.dto.request.CreateProjectRequest;
import com.example.definex.taskmanagement.dto.request.UpdateProjectRequest;
import com.example.definex.taskmanagement.dto.response.CreatedProjectResponse;
import com.example.definex.taskmanagement.dto.response.ProjectResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedProjectResponse;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.Project;
import com.example.definex.taskmanagement.exception.DepartmentNotFoundException;
import com.example.definex.taskmanagement.exception.ProjectNotFoundException;
import com.example.definex.taskmanagement.exception.ProjectValidationException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.DepartmentRepository;
import com.example.definex.taskmanagement.repository.ProjectRepository;
import com.example.definex.taskmanagement.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ProjectAuthorization projectAuthorization;
    private final DepartmentRepository departmentRepository;

    @Override
    public CreatedProjectResponse save(CreateProjectRequest createProjectRequest,Long departmentId){

        if(createProjectRequest.getTitle()==null||createProjectRequest.getTitle().trim().isEmpty()){
            throw new ProjectValidationException(MessageKey.PROJECT_TITLE_CANNOT_BE_EMPTY.toString());
        }

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(()->new DepartmentNotFoundException(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.toString()));


        Project project = projectMapper.createProjectRequestToProject(createProjectRequest);
        project.setDepartment(department);
        projectAuthorization.userHasAuthorization(project);

        projectRepository.save(project);
        return projectMapper.projectToCreatedProjectResponse(project);
    }
    @Override
    public UpdatedProjectResponse update(UpdateProjectRequest updateProjectRequest,Long projectId){

        Project project = projectRepository.findById(projectId).orElseThrow(()->new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.toString()));

        projectAuthorization.userHasAuthorization(project);

        project.setTitle(updateProjectRequest.getTitle());
        project.setDescription(updateProjectRequest.getDescription());
        project.setType(updateProjectRequest.getType());

        return projectMapper.projectToUpdatedProjectResponse(projectRepository.save(project));
    }
    @Override
    public ProjectResponse findById(Long id){

        Project project = projectRepository.findById(id).orElseThrow(()->new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.toString()));

        projectAuthorization.userHasAuthorization(project);

        return projectMapper.projectToProjectResponse(project);
    }
    @Override
    public void deleteById(Long id){
        Project project = projectRepository.findById(id).orElseThrow(()->new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.toString()));

        projectAuthorization.userHasAuthorization(project);

        project.setIsDeleted(true);
        projectRepository.save(project);
    }
}
