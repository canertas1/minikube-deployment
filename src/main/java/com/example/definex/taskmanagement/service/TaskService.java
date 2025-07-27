package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.CreateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskStateRequest;
import com.example.definex.taskmanagement.dto.response.CreatedTaskResponse;
import com.example.definex.taskmanagement.dto.response.TaskResponse;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.TaskPriorityType;
import com.example.definex.taskmanagement.entities.TaskStateType;

public interface TaskService {
    CreatedTaskResponse save(CreateTaskRequest createTaskRequest,Long projectId);
    TaskResponse findById(Long id);
    TaskResponse updateTaskState(UpdateTaskStateRequest updateTaskStateRequest, Long taskId);
    TaskResponse updateTask(Long taskId, UpdateTaskRequest updateTaskRequest);
    TaskResponse assignTaskToTeamMember(Long taskId, Long userId);
    TaskResponse changeTaskPriority(Long taskId, TaskPriorityType taskPriorityType);
    void deleteById(Long taskId);
}
