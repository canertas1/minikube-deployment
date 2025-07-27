package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.mapper.DepartmentMapper;
import com.example.definex.taskmanagement.dto.request.CreateDepartmentRequest;
import com.example.definex.taskmanagement.dto.response.CreatedDepartmentResponse;
import com.example.definex.taskmanagement.dto.response.DepartmentResponse;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.exception.DepartmentNotFoundException;
import com.example.definex.taskmanagement.exception.DepartmentValidationException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.DepartmentRepository;
import com.example.definex.taskmanagement.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class DepartmentServiceImplTest {
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private DepartmentMapper departmentMapper;
    @InjectMocks
    private DepartmentServiceImpl departmentService;
    private Department department;
    private CreateDepartmentRequest createDepartmentRequest;
    private CreatedDepartmentResponse createdDepartmentResponse;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("IT");
        department.setIsDeleted(false);

        createDepartmentRequest = new CreateDepartmentRequest();
        createDepartmentRequest.setName("IT");

        createdDepartmentResponse = new CreatedDepartmentResponse();
        createdDepartmentResponse.setName("IT");

        departmentResponse = new DepartmentResponse();
        departmentResponse.setName("IT");
    }

    @Test
    void save_ValidRequest_ShouldReturnCreatedDepartmentResponse() {
        when(departmentMapper.createDepartmentRequestToDepartment(any(CreateDepartmentRequest.class))).thenReturn(department);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.departmentToCreatedDepartmentResponse(any(Department.class))).thenReturn(createdDepartmentResponse);

        CreatedDepartmentResponse result = departmentService.save(createDepartmentRequest);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        verify(departmentMapper).createDepartmentRequestToDepartment(createDepartmentRequest);
        verify(departmentRepository).save(department);
        verify(departmentMapper).departmentToCreatedDepartmentResponse(department);
    }

    @Test
    void save_EmptyName_ShouldThrowDepartmentValidationException() {
        CreateDepartmentRequest emptyRequest = new CreateDepartmentRequest();
        emptyRequest.setName("");

        DepartmentValidationException exception = assertThrows(DepartmentValidationException.class, () -> {
            departmentService.save(emptyRequest);
        });
        assertEquals(MessageKey.DEPARTMENT_NAME_CANNOT_BE_EMPTY.toString(), exception.getMessage());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void save_NullName_ShouldThrowDepartmentValidationException() {
        CreateDepartmentRequest nullRequest = new CreateDepartmentRequest();
        nullRequest.setName(null);

        DepartmentValidationException exception = assertThrows(DepartmentValidationException.class, () -> {
            departmentService.save(nullRequest);
        });
        assertEquals(MessageKey.DEPARTMENT_NAME_CANNOT_BE_EMPTY.toString(), exception.getMessage());
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void findById_ExistingId_ShouldReturnDepartmentResponse() {
        Long id = 1L;
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(departmentMapper.departmentToDepartmentResponse(any(Department.class))).thenReturn(departmentResponse);

        DepartmentResponse result = departmentService.findById(id);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        verify(departmentRepository).findById(id);
        verify(departmentMapper).departmentToDepartmentResponse(department);
    }

    @Test
    void findById_NonExistingId_ShouldThrowDepartmentNotFoundException() {
        Long id = 999L;
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        DepartmentNotFoundException exception = assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.findById(id);
        });
        assertEquals(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.toString(), exception.getMessage());
    }

    @Test
    void findAll_ShouldReturnPageOfDepartmentResponses() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Department> departmentPage = new PageImpl<>(List.of(department));

        when(departmentRepository.findAll(pageable)).thenReturn(departmentPage);
        when(departmentMapper.departmentToDepartmentResponse(department)).thenReturn(departmentResponse);

        Page<DepartmentResponse> result = departmentService.findAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(departmentResponse, result.getContent().get(0));
        verify(departmentRepository).findAll(pageable);
        verify(departmentMapper).departmentToDepartmentResponse(department);
    }

    @Test
    void deleteById_ExistingId_ShouldMarkAsDeleted() {
        Long id = 1L;
        when(departmentRepository.findById(id)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        departmentService.deleteById(id);

        assertTrue(department.getIsDeleted());
        verify(departmentRepository).findById(id);
        verify(departmentRepository).save(department);
    }

    @Test
    void deleteById_NonExistingId_ShouldThrowDepartmentNotFoundException() {
        Long id = 999L;
        when(departmentRepository.findById(id)).thenReturn(Optional.empty());

        DepartmentNotFoundException exception = assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.deleteById(id);
        });
        assertEquals(MessageKey.DEPARTMENT_NOT_FOUND_WITH_ID.toString() + id, exception.getMessage());
        verify(departmentRepository, never()).save(any());
    }
}