package com.example.definex.taskmanagement.controller;
import com.example.definex.taskmanagement.dto.request.CreateUserRequest;
import com.example.definex.taskmanagement.dto.response.CreatedUserResponse;
import com.example.definex.taskmanagement.dto.response.UserResponse;
import com.example.definex.taskmanagement.exception.GlobalExceptionHandler;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.service.UserService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
 class UserControllerTest {

    private static final String API_BASE_PATH = "/api/users";

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("test");
        request.setEmail("test@gmail.com");
        request.setPassword("test");

        CreatedUserResponse response = new CreatedUserResponse();
        response.setName("test");
        response.setEmail("test@gmail.com");

        when(userService.save(any(CreateUserRequest.class))).thenReturn(response);

        mockMvc.perform(post(API_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(userService, times(1)).save(any(CreateUserRequest.class));
    }
    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        Long userId = 1L;
        UserResponse userResponse = new UserResponse();

        when(userService.getById(userId)).thenReturn(userResponse);

        mockMvc.perform(get(API_BASE_PATH + "/{id}", userId))
                .andExpect(status().isOk());

        verify(userService, times(1)).getById(eq(userId));
    }
    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteById(userId);

        mockMvc.perform(delete(API_BASE_PATH + "/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteById(eq(userId));
    }
    @Test
    void getAllUsers_ShouldReturnPagedUsers() throws Exception {
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        UserResponse userResponse = new UserResponse();

        Page<UserResponse> pagedResponse = new PageImpl<>(
                Collections.singletonList(userResponse),
                pageable,
                1L
        );

        when(userService.findAll(eq(pageable))).thenReturn(pagedResponse);

        mockMvc.perform(get(API_BASE_PATH)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk());
        verify(userService, times(1)).findAll(eq(pageable));
    }
    @Test
    void getUserById_WhenNotExists_ShouldReturn_UserNotFoundException() throws Exception {
        Long invalidId = 999L;
        when(userService.getById(invalidId)).thenThrow(new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.getMessage()));

        mockMvc.perform(get(API_BASE_PATH + "/{id}", invalidId))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteUser_WhenNotExists_ShouldReturnNotFound() throws Exception {
        Long invalidId = 1L;
        doThrow(new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.getMessage())).when(userService).deleteById(invalidId);

        mockMvc.perform(delete(API_BASE_PATH + "/{id}", invalidId))
                .andExpect(status().isNotFound());
    }
}



