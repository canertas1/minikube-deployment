package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateDepartmentRequest;
import com.example.definex.taskmanagement.dto.response.CreatedDepartmentResponse;
import com.example.definex.taskmanagement.dto.response.DepartmentResponse;
import com.example.definex.taskmanagement.exception.DepartmentNotFoundException;
import com.example.definex.taskmanagement.exception.GlobalExceptionHandler;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.service.DepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 class DepartmentControllerTest {
    private static final String API_BASE_PATH = "/api/departments";

    @InjectMocks
    private DepartmentController departmentController;

    @Mock
    private DepartmentService departmentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    void createDepartment_ValidRequest_ReturnsCreatedDepartment() throws Exception {
        CreateDepartmentRequest request = new CreateDepartmentRequest("IT");
        CreatedDepartmentResponse response = new CreatedDepartmentResponse();

        when(departmentService.save(request)).thenReturn(response);

        mockMvc.perform(post(API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(departmentService).save(request);
    }
    @Test
    void getDepartmentById_ExistingId_ReturnsDepartmentResponse() throws Exception {
        Long id = 1L;
        DepartmentResponse response = new DepartmentResponse();
        when(departmentService.findById(id)).thenReturn(response);

        mockMvc.perform(get(API_BASE_PATH + "/{id}", id))
                .andExpect(status().isOk());

        verify(departmentService).findById(id);
    }
    @Test
    void getDepartmentById_NonExistingId_ReturnsNotFound() throws Exception {
        Long id = 99L;
        when(departmentService.findById(id)).thenThrow(new DepartmentNotFoundException(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.getMessage()));

        mockMvc.perform(get(API_BASE_PATH + "/{id}", id))
                .andExpect(status().isNotFound());

        verify(departmentService).findById(id);
    }
    @Test
    void getAllDepartments_DefaultPagination_ReturnsPage() throws Exception {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        DepartmentResponse departmentResponse = new DepartmentResponse();

        Page<DepartmentResponse> pagedResponse = new PageImpl<>(
                Collections.singletonList(departmentResponse),
                pageable,
                1L
        );

        when(departmentService.findAll(eq(pageable))).thenReturn(pagedResponse);

        mockMvc.perform(get(API_BASE_PATH)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());
        verify(departmentService, times(1)).findAll(eq(pageable));
    }
    @Test
    void deleteDepartment_ExistingId_ReturnsNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(departmentService).deleteById(id);

        mockMvc.perform(delete(API_BASE_PATH + "/{id}", id))
                .andExpect(status().isNoContent());

        verify(departmentService).deleteById(id);
    }
    @Test
    void deleteDepartment_NonExistingId_ShouldThrowDepartmentNotFoundException() throws Exception {
        Long id = 99L;
        doThrow(new DepartmentNotFoundException(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.getMessage())).when(departmentService).deleteById(id);

        mockMvc.perform(delete(API_BASE_PATH + "/{id}", id))
                .andExpect(status().isNotFound());

        verify(departmentService).deleteById(id);
    }
}
