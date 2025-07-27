package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.mapper.UserMapper;
import com.example.definex.taskmanagement.dto.request.CreateUserRequest;
import com.example.definex.taskmanagement.dto.request.UpdateUserRequest;
import com.example.definex.taskmanagement.dto.response.CreatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UserResponse;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.DepartmentNotFoundException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.repository.DepartmentRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
 class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(userId);
        user.setIsDeleted(false);
    }

    @Test
    void createUser_ShouldReturnCreatedUserResponse() {
        CreateUserRequest request = new CreateUserRequest();
        CreatedUserResponse expectedResponse = new CreatedUserResponse();

        when(userMapper.createUserRequestToUser(any(CreateUserRequest.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToCreatedUserResponse(any(User.class))).thenReturn(expectedResponse);

        CreatedUserResponse actualResponse = userService.save(request);

        assertSame(expectedResponse, actualResponse);
        verify(userMapper).createUserRequestToUser(request);
        verify(userRepository).save(user);
        verify(userMapper).userToCreatedUserResponse(user);
    }

    @Test
    void getById_WhenUserExists_ShouldReturnUserResponse() {
        UserResponse expectedResponse = new UserResponse();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(expectedResponse);

        UserResponse actualResponse = userService.getById(userId);

        assertSame(expectedResponse, actualResponse);
        verify(userRepository).findById(userId);
        verify(userMapper).userToUserResponse(user);
    }

    @Test
    void getById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void deleteById_WhenUserExists_ShouldSoftDelete() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        assertTrue(user.getIsDeleted());
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    void deleteById_WhenUserNotExists_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteById(userId));
        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any());
    }

    @Test
    void findAll_ShouldReturnPageOfUserResponses() {
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        UserResponse userResponse = new UserResponse();

        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(userMapper.userToUserResponse(any(User.class))).thenReturn(userResponse);

        Page<UserResponse> resultPage = userService.findAll(Pageable.unpaged());

        assertEquals(1, resultPage.getTotalElements());
        assertSame(userResponse, resultPage.getContent().get(0));
        verify(userRepository).findAll(Pageable.unpaged());
        verify(userMapper).userToUserResponse(user);
    }
    @Test
    void update_ShouldReturnUpdatedUserResponse() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");
        request.setEmail("updated@email.com");
        request.setDepartmentId(2L);

        Department department = new Department();
        department.setId(2L);

        UpdatedUserResponse expectedResponse = new UpdatedUserResponse();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.of(department));
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToUpdatedUserResponse(any(User.class))).thenReturn(expectedResponse);

        UpdatedUserResponse actualResponse = userService.update(request, userId);

        assertSame(expectedResponse, actualResponse);
        verify(userRepository).findById(userId);
        verify(departmentRepository).findById(request.getDepartmentId());
        verify(userRepository).save(user);
        verify(userMapper).userToUpdatedUserResponse(user);

        assertEquals("Updated Name", user.getName());
        assertEquals("updated@email.com", user.getEmail());
        assertEquals(department, user.getDepartment());
    }
    @Test
    void update_WithNonExistingUser_ShouldThrowUserNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.update(request, userId));
        verify(userRepository).findById(userId);
        verify(departmentRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void update_WithNonExistingDepartment_ShouldThrowDepartmentNotFoundException() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setDepartmentId(2L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(departmentRepository.findById(request.getDepartmentId())).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> userService.update(request, userId));
        verify(userRepository).findById(userId);
        verify(departmentRepository).findById(request.getDepartmentId());
        verify(userRepository, never()).save(any(User.class));
    }
}
