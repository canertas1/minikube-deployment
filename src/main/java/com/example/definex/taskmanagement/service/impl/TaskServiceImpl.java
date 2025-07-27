package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.authorization.TaskAuthorization;
import com.example.definex.taskmanagement.dto.request.CreateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskStateRequest;
import com.example.definex.taskmanagement.dto.response.CreatedTaskResponse;
import com.example.definex.taskmanagement.dto.mapper.TaskMapper;
import com.example.definex.taskmanagement.dto.response.TaskResponse;
import com.example.definex.taskmanagement.entities.*;
import com.example.definex.taskmanagement.exception.ProjectNotFoundException;
import com.example.definex.taskmanagement.exception.TaskNotFoundException;
import com.example.definex.taskmanagement.exception.TaskValidationException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.ProjectRepository;
import com.example.definex.taskmanagement.repository.TaskRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskAuthorization taskAuthorization;
    private final ProjectRepository projectRepository;
    private final TaskMapper taskMapper;

    @Override
    public CreatedTaskResponse save(CreateTaskRequest createTaskRequest,Long projectId){

        Project project = projectRepository.findById(projectId).orElseThrow
                (()->new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.toString()));

        taskAuthorization.canCreateTask(project);

        Task task = taskMapper.createdTaskRequestToTask(createTaskRequest);
        task.setProject(project);

        Task savedTask = taskRepository.save(task);

        return taskMapper.taskToCreatedTaskResponse(savedTask);
    }
    @Override
    public TaskResponse findById(Long id){
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString() + id));

        taskAuthorization.validateTaskAccess(task);

        return taskMapper.taskToTaskResponse(task);
    }
    @Override
    public TaskResponse updateTaskState(UpdateTaskStateRequest updateTaskStateRequest,Long taskId){

        TaskStateType newState = updateTaskStateRequest.getNewState();
        String reason = updateTaskStateRequest.getReason();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        if ((newState == TaskStateType.CANCELLED || newState == TaskStateType.BLOCKED) &&
                (reason == null || reason.trim().isEmpty())) {
            throw new TaskValidationException(MessageKey.REASON_IS_REQUIRED_FOR_CANCEL_OR_BLOCK_STATE.toString());
        }

        taskAuthorization.validateTaskStateChange(task, newState);

        task.setState(newState);
        task.setStateChangeReason(reason);
        return taskMapper.taskToTaskResponse(taskRepository.save(task));
    }
    @Override
    public TaskResponse updateTask(Long taskId, UpdateTaskRequest updateTaskRequest){
        Task existingTask  = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        taskAuthorization.validateTaskManagement(existingTask);
        existingTask.setTitle(updateTaskRequest.getTitle());

        return taskMapper.taskToTaskResponse(taskRepository.save(existingTask));
    }
    @Override
    public TaskResponse assignTaskToTeamMember(Long taskId, Long userId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.toString()+userId));

        taskAuthorization.validateTaskAssignment(task);

        task.setAssignee(user);
        return taskMapper.taskToTaskResponse(taskRepository.save(task));
    }
    @Override
    public TaskResponse changeTaskPriority(Long taskId, TaskPriorityType taskPriorityType){
        Task task  = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        taskAuthorization.validateTaskPriorityChange(task);

        task.setPriority(taskPriorityType);
        return taskMapper.taskToTaskResponse(taskRepository.save(task));
    }
    @Override
    public void deleteById(Long taskId){
        Task deletedTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        taskAuthorization.validateTaskDeletion(deletedTask);

        deletedTask.setIsDeleted(true);
        taskRepository.save(deletedTask);
    }
}