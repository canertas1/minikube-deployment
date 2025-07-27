package com.example.definex.taskmanagement.dto.mapper;

import com.example.definex.taskmanagement.dto.request.CreateTaskRequest;
import com.example.definex.taskmanagement.dto.response.CreatedTaskResponse;
import com.example.definex.taskmanagement.dto.response.TaskResponse;
import com.example.definex.taskmanagement.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "project", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "stateChangeReason", ignore = true)
    Task createdTaskRequestToTask(CreateTaskRequest createTaskRequest);
    CreatedTaskResponse taskToCreatedTaskResponse(Task task);
    @Mapping(source = "project.id", target = "projectId")
    TaskResponse taskToTaskResponse(Task task);
}
