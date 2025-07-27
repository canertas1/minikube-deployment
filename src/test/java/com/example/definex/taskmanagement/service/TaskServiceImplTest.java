package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.authorization.impl.TaskAuthorizationImpl;
import com.example.definex.taskmanagement.dto.mapper.TaskMapper;
import com.example.definex.taskmanagement.dto.request.CreateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskStateRequest;
import com.example.definex.taskmanagement.dto.response.CreatedTaskResponse;
import com.example.definex.taskmanagement.dto.response.TaskResponse;
import com.example.definex.taskmanagement.entities.*;
import com.example.definex.taskmanagement.exception.*;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.ProjectRepository;
import com.example.definex.taskmanagement.repository.TaskRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class TaskServiceImplTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TaskAuthorizationImpl taskAuthorizationImpl;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    private User testUser;
    private Project testProject;
    private Task testTask;
    private Department testDepartment;
    private CreateTaskRequest createTaskRequest;
    private UpdateTaskRequest updateTaskRequest;
    private UpdateTaskStateRequest updateTaskStateRequest;
    private TaskResponse taskResponse;
    private CreatedTaskResponse createdTaskResponse;

    @BeforeEach
    public void setUp() {
        testDepartment = new Department();
        testDepartment.setId(1L);
        testDepartment.setName("Test Department");

        testUser = new User();
        testUser.setId(1L);
        testUser.setRole(Role.TEAM_LEADER);
        testUser.setDepartment(testDepartment);

        testProject = new Project();
        testProject.setId(1L);
        testProject.setTitle("Test Project");
        testProject.setDepartment(testDepartment);

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setState(TaskStateType.BACKLOG);
        testTask.setPriority(TaskPriorityType.MEDIUM);
        testTask.setProject(testProject);
        testTask.setAssignee(testUser);
        testTask.setIsDeleted(false);

        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("New Task");

        updateTaskRequest = new UpdateTaskRequest();
        updateTaskRequest.setTitle("Updated Task");

        updateTaskStateRequest = new UpdateTaskStateRequest();
        updateTaskStateRequest.setNewState(TaskStateType.IN_PROGRESS);
        updateTaskStateRequest.setReason("Starting work");

        taskResponse = new TaskResponse();
        taskResponse.setTitle("Test Task");

        createdTaskResponse = new CreatedTaskResponse();
        createdTaskResponse.setTitle("New Task");
    }

    @Test
     void save_WhenAuthorized_ShouldReturnCreatedTaskResponse() {

        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        doNothing().when(taskAuthorizationImpl).canCreateTask(testProject);
        when(taskMapper.createdTaskRequestToTask(createTaskRequest)).thenReturn(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.taskToCreatedTaskResponse(testTask)).thenReturn(createdTaskResponse);

        CreatedTaskResponse result = taskService.save(createTaskRequest, 1L);

        assertNotNull(result);
        assertEquals(createdTaskResponse, result);
        verify(projectRepository).findById(1L);
        verify(taskAuthorizationImpl).canCreateTask(testProject);
        verify(taskMapper).createdTaskRequestToTask(createTaskRequest);
        verify(taskRepository).save(testTask);
        verify(taskMapper).taskToCreatedTaskResponse(testTask);
    }

    @Test
     void save_WhenProjectNotFound_ShouldThrowProjectNotFoundException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        ProjectNotFoundException exception = assertThrows(
                ProjectNotFoundException.class,
                () -> taskService.save(createTaskRequest, 1L)
        );
        assertTrue(exception.getMessage().contains(MessageKey.PROJECT_NOT_FOUND_WITH_ID.toString()));
        verify(projectRepository).findById(1L);
        verifyNoInteractions(taskMapper, taskRepository);
    }

    @Test
     void save_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_TASK.toString()))
                .when(taskAuthorizationImpl).canCreateTask(testProject);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> taskService.save(createTaskRequest, 1L)
        );
        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_TASK.toString(), exception.getMessage());
        verify(projectRepository).findById(1L);
        verify(taskAuthorizationImpl).canCreateTask(testProject);
        verifyNoInteractions(taskMapper, taskRepository);
    }

    @Test
     void findById_WhenAuthorized_ShouldReturnTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskAuthorizationImpl).validateTaskAccess(testTask);
        when(taskMapper.taskToTaskResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals(taskResponse, result);
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskAccess(testTask);
        verify(taskMapper).taskToTaskResponse(testTask);
    }

    @Test
     void findById_WhenTaskNotFound_ShouldThrowTaskNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        TaskNotFoundException exception = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.findById(1L)
        );
        assertTrue(exception.getMessage().contains(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()));
        verify(taskRepository).findById(1L);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void findById_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_ACCESS_TO_TASK.toString()))
                .when(taskAuthorizationImpl).validateTaskAccess(testTask);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> taskService.findById(1L)
        );
        assertEquals(MessageKey.USER_DOES_NOT_HAVE_ACCESS_TO_TASK.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskAccess(testTask);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void updateTaskState_WhenValidStateTransition_ShouldReturnUpdatedTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskAuthorizationImpl).validateTaskStateChange(eq(testTask), any(TaskStateType.class));
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.taskToTaskResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTaskState(updateTaskStateRequest, 1L);

        assertNotNull(result);
        assertEquals(taskResponse, result);
        assertEquals(TaskStateType.IN_PROGRESS, testTask.getState());
        assertEquals("Starting work", testTask.getStateChangeReason());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskStateChange(testTask, TaskStateType.IN_PROGRESS);
        verify(taskRepository).save(testTask);
        verify(taskMapper).taskToTaskResponse(testTask);
    }

    @Test
     void updateTaskState_WhenCancelledWithoutReason_ShouldThrowTaskValidationException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        updateTaskStateRequest.setNewState(TaskStateType.CANCELLED);
        updateTaskStateRequest.setReason("");

        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> taskService.updateTaskState(updateTaskStateRequest, 1L)
        );
        assertEquals(MessageKey.REASON_IS_REQUIRED_FOR_CANCEL_OR_BLOCK_STATE.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void updateTaskState_WhenBlockedWithoutReason_ShouldThrowTaskValidationException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        updateTaskStateRequest.setNewState(TaskStateType.BLOCKED);
        updateTaskStateRequest.setReason(null);

        TaskValidationException exception = assertThrows(
                TaskValidationException.class,
                () -> taskService.updateTaskState(updateTaskStateRequest, 1L)
        );
        assertEquals(MessageKey.REASON_IS_REQUIRED_FOR_CANCEL_OR_BLOCK_STATE.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void updateTaskState_WhenInvalidStateTransition_ShouldThrowInvalidTaskStateTransitionException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doThrow(new InvalidTaskStateTransitionException(testTask.getState(), updateTaskStateRequest.getNewState()))
                .when(taskAuthorizationImpl).validateTaskStateChange(eq(testTask), any(TaskStateType.class));

        assertThrows(
                InvalidTaskStateTransitionException.class,
                () -> taskService.updateTaskState(updateTaskStateRequest, 1L)
        );
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskStateChange(testTask, TaskStateType.IN_PROGRESS);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void updateTask_WhenAuthorized_ShouldReturnUpdatedTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskAuthorizationImpl).validateTaskManagement(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.taskToTaskResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.updateTask(1L, updateTaskRequest);

        assertNotNull(result);
        assertEquals(taskResponse, result);
        assertEquals("Updated Task", testTask.getTitle());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskManagement(testTask);
        verify(taskRepository).save(testTask);
        verify(taskMapper).taskToTaskResponse(testTask);
    }

    @Test
     void updateTask_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_MANAGE_TASK.toString()))
                .when(taskAuthorizationImpl).validateTaskManagement(testTask);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> taskService.updateTask(1L, updateTaskRequest)
        );
        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_MANAGE_TASK.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskManagement(testTask);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void assignTaskToTeamMember_WhenAuthorized_ShouldReturnUpdatedTaskResponse() {
        User newAssignee = new User();
        newAssignee.setId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newAssignee));
        doNothing().when(taskAuthorizationImpl).validateTaskAssignment(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.taskToTaskResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.assignTaskToTeamMember(1L, 2L);

        assertNotNull(result);
        assertEquals(taskResponse, result);
        assertEquals(newAssignee, testTask.getAssignee());
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(taskAuthorizationImpl).validateTaskAssignment(testTask);
        verify(taskRepository).save(testTask);
        verify(taskMapper).taskToTaskResponse(testTask);
    }

    @Test
     void assignTaskToTeamMember_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> taskService.assignTaskToTeamMember(1L, 2L)
        );
        assertTrue(exception.getMessage().contains(MessageKey.USER_NOT_FOUND_WITH_ID.toString()));
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(2L);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void assignTaskToTeamMember_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        User newAssignee = new User();
        newAssignee.setId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newAssignee));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_ASSIGN_TASK.toString()))
                .when(taskAuthorizationImpl).validateTaskAssignment(testTask);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> taskService.assignTaskToTeamMember(1L, 2L)
        );
        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_ASSIGN_TASK.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(taskAuthorizationImpl).validateTaskAssignment(testTask);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void changeTaskPriority_WhenAuthorized_ShouldReturnUpdatedTaskResponse() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskAuthorizationImpl).validateTaskPriorityChange(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);
        when(taskMapper.taskToTaskResponse(testTask)).thenReturn(taskResponse);

        TaskResponse result = taskService.changeTaskPriority(1L, TaskPriorityType.HIGH);

        assertNotNull(result);
        assertEquals(taskResponse, result);
        assertEquals(TaskPriorityType.HIGH, testTask.getPriority());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskPriorityChange(testTask);
        verify(taskRepository).save(testTask);
        verify(taskMapper).taskToTaskResponse(testTask);
    }

    @Test
     void changeTaskPriority_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_TASK_PRIORITY.toString()))
                .when(taskAuthorizationImpl).validateTaskPriorityChange(testTask);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> taskService.changeTaskPriority(1L, TaskPriorityType.HIGH)
        );
        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_TASK_PRIORITY.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskPriorityChange(testTask);
        verifyNoMoreInteractions(taskRepository);
        verifyNoInteractions(taskMapper);
    }

    @Test
     void deleteById_WhenAuthorized_ShouldMarkTaskAsDeleted() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doNothing().when(taskAuthorizationImpl).validateTaskDeletion(testTask);
        when(taskRepository.save(testTask)).thenReturn(testTask);

        taskService.deleteById(1L);

        assertTrue(testTask.getIsDeleted());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskDeletion(testTask);
        verify(taskRepository).save(testTask);
    }

    @Test
     void deleteById_WhenUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_DELETE_TASK.toString()))
                .when(taskAuthorizationImpl).validateTaskDeletion(testTask);

        UnauthorizedAccessException exception = assertThrows(
                UnauthorizedAccessException.class,
                () -> taskService.deleteById(1L)
        );
        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_DELETE_TASK.toString(), exception.getMessage());
        verify(taskRepository).findById(1L);
        verify(taskAuthorizationImpl).validateTaskDeletion(testTask);
        verifyNoMoreInteractions(taskRepository);
    }
}