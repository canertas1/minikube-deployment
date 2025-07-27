package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskStateRequest;
import com.example.definex.taskmanagement.dto.response.CreatedTaskResponse;
import com.example.definex.taskmanagement.dto.response.TaskResponse;
import com.example.definex.taskmanagement.entities.TaskPriorityType;
import com.example.definex.taskmanagement.entities.TaskStateType;
import com.example.definex.taskmanagement.exception.GlobalExceptionHandler;
import com.example.definex.taskmanagement.exception.TaskNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 class TaskControllerTest {
    private static final String API_BASE_PATH = "/api/tasks";
    @InjectMocks
    private TaskController taskController;
    @Mock
    private TaskService taskService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    void createTask_ValidRequest_ReturnsCreatedTaskResponse() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("test");

        CreatedTaskResponse mockResponse = new CreatedTaskResponse();
        when(taskService.save(any(CreateTaskRequest.class), anyLong())).thenReturn(mockResponse);

        mockMvc.perform(post(API_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(taskService, times(1)).save(any(), anyLong());
    }
    @Test
    void getTaskById_WhenTaskExists_ReturnsTaskResponse() throws Exception {
        TaskResponse mockResponse = new TaskResponse();
        when(taskService.findById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).findById(1L);
    }
    @Test
    void getTaskById_WhenTaskNotFound_ReturnsNotFound() throws Exception {
        when(taskService.findById(1L)).thenThrow(new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.getMessage()));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).findById(1L);
    }
    @Test
    void updateTaskState_ValidRequest_ReturnsUpdatedTask() throws Exception {

        UpdateTaskStateRequest request = new UpdateTaskStateRequest();
        request.setNewState(TaskStateType.IN_PROGRESS);

        TaskResponse mockResponse = new TaskResponse();
        mockResponse.setState(TaskStateType.IN_PROGRESS);

        when(taskService.updateTaskState(any(UpdateTaskStateRequest.class), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(put(API_BASE_PATH + "/1/state")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("IN_PROGRESS"));

        verify(taskService, times(1)).updateTaskState(any(), eq(1L));
    }
    @Test
    void updateTask_ValidRequest_ReturnsUpdatedTask() throws Exception {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("test");

        TaskResponse mockResponse = new TaskResponse();
        mockResponse.setTitle("test");

        when(taskService.updateTask(eq(1L), any(UpdateTaskRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(put(API_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("test"));

        verify(taskService, times(1)).updateTask(eq(1L), any());
    }
    @Test
    void assignTaskToUser_ValidIds_ReturnsUpdatedTask() throws Exception {
        TaskResponse mockResponse = new TaskResponse();

        when(taskService.assignTaskToTeamMember(1L, 2L)).thenReturn(mockResponse);

        mockMvc.perform(patch(API_BASE_PATH + "/1/assign/2"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).assignTaskToTeamMember(1L, 2L);
    }
    @Test
    void changeTaskPriority_ValidRequest_ReturnsUpdatedTask() throws Exception {
        TaskResponse mockResponse = new TaskResponse();
        mockResponse.setPriority(TaskPriorityType.HIGH);
        when(taskService.changeTaskPriority(1L, TaskPriorityType.HIGH)).thenReturn(mockResponse);

        mockMvc.perform(put(API_BASE_PATH + "/1/priority")
                        .param("priority", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.priority").value("HIGH"));

        verify(taskService, times(1)).changeTaskPriority(1L, TaskPriorityType.HIGH);
    }
    @Test
    void deleteTask_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(taskService).deleteById(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNoContent());

        verify(taskService, times(1)).deleteById(1L);
    }
    @Test
    void deleteTask_NonExistingId_ReturnsNotFound() throws Exception {
        doThrow(new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.getMessage())).when(taskService).deleteById(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());

        verify(taskService, times(1)).deleteById(1L);
    }
}