package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateProjectRequest;
import com.example.definex.taskmanagement.dto.request.UpdateProjectRequest;
import com.example.definex.taskmanagement.dto.response.CreatedProjectResponse;
import com.example.definex.taskmanagement.dto.response.ProjectResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedProjectResponse;
import com.example.definex.taskmanagement.exception.GlobalExceptionHandler;
import com.example.definex.taskmanagement.exception.ProjectNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.service.ProjectService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

 class ProjectControllerTest {
    private static final String API_BASE_PATH = "/api/projects";
    @InjectMocks
    private ProjectController projectController;
    @Mock
    private ProjectService projectService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(projectController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    void createProject_ValidRequest_ReturnsCreatedProject() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest("Project Alpha", "PA1");
        CreatedProjectResponse mockResponse = new CreatedProjectResponse();

        when(projectService.save(any(CreateProjectRequest.class), eq(1L)))
                .thenReturn(mockResponse);

        mockMvc.perform(post(API_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectById_ExistingId_ReturnsProject() throws Exception {

        ProjectResponse mockResponse = new ProjectResponse();
        when(projectService.findById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isOk());
    }
    @Test

    void getProjectById_NonExistingId_ReturnsNotFound() throws Exception {
        when(projectService.findById(1L))
                .thenThrow(new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.getMessage()));

        mockMvc.perform(get(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteProject_ExistingId_ReturnsNoContent() throws Exception {
        doNothing().when(projectService).deleteById(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNoContent());

        verify(projectService, times(1)).deleteById(1L);
    }
    @Test
    void deleteProject_NonExistingId_ReturnsNotFound() throws Exception {
        doThrow(new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.getMessage()))
                .when(projectService).deleteById(1L);

        mockMvc.perform(delete(API_BASE_PATH + "/1"))
                .andExpect(status().isNotFound());
    }
     @Test
     void updateProject_ValidRequest_ReturnsUpdatedProject() throws Exception {
         UpdateProjectRequest request = new UpdateProjectRequest();
         request.setTitle("Updated Title");
         request.setDescription("Updated description");

         UpdatedProjectResponse mockResponse = new UpdatedProjectResponse();
         mockResponse.setTitle("Updated Title");
         mockResponse.setDescription("Updated description");

         when(projectService.update(any(UpdateProjectRequest.class), eq(1L)))
                 .thenReturn(mockResponse);

         mockMvc.perform(post(API_BASE_PATH + "/update/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                 .andExpect(status().isOk());
     }
     @Test
     void updateProject_InvalidProjectId_ReturnsNotFound() throws Exception {
         UpdateProjectRequest request = new UpdateProjectRequest();
         request.setTitle("Updated Title");

         when(projectService.update(any(UpdateProjectRequest.class), eq(1L)))
                 .thenThrow(new ProjectNotFoundException(MessageKey.PROJECT_NOT_FOUND_WITH_ID.getMessage()));

         mockMvc.perform(post(API_BASE_PATH + "/update/1")
                         .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(request)))
                 .andExpect(status().isNotFound());
     }
}
