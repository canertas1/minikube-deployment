package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.response.FileAttachmentResponse;
import com.example.definex.taskmanagement.dto.response.UploadedFileAttachmentResponse;
import com.example.definex.taskmanagement.exception.AttachmentNotFoundException;
import com.example.definex.taskmanagement.exception.GlobalExceptionHandler;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.service.AttachmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

 class AttachmentControllerTest {
    private static final String API_BASE_PATH = "/api/attachments";

    @InjectMocks
    private AttachmentController attachmentController;

    @Mock
    private AttachmentService attachmentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(attachmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }
    @Test
    void uploadFile_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "Test Content".getBytes()
        );
        Long userId = 1L;
        Long taskId = 1L;
        UploadedFileAttachmentResponse mockResponse = new UploadedFileAttachmentResponse();

        when(attachmentService.uploadFile(any(), any())).thenReturn(mockResponse);

        mockMvc.perform(multipart(API_BASE_PATH + "/upload")
                        .file(file)
                        .param("userId", userId.toString())
                        .param("taskId", taskId.toString()))
                .andExpect(status().isOk());
    }
    @Test
    void downloadFile_Success() throws Exception {
        Long attachmentId = 1L;
        Resource mockResource = new ByteArrayResource("File Content".getBytes());
        when(attachmentService.downloadFile(attachmentId)).thenReturn(mockResource);

        mockMvc.perform(get(API_BASE_PATH + "/download/{attachmentId}", attachmentId))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"null\""))
                .andExpect(content().bytes("File Content".getBytes()));
    }
    @Test
    void downloadFile_NotFound_ReturnsNotFound() throws Exception {
        Long attachmentId = 999L;
        when(attachmentService.downloadFile(attachmentId))
                .thenThrow(new AttachmentNotFoundException(MessageKey.ATTACHMENT_NOT_FOUND_WITH_ID.getMessage()));

        mockMvc.perform(get(API_BASE_PATH + "/download/{attachmentId}", attachmentId))
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteAttachment_Success() throws Exception {
        Long attachmentId = 1L;
        doNothing().when(attachmentService).deleteAttachment(attachmentId);

        mockMvc.perform(delete(API_BASE_PATH + "/{attachmentId}", attachmentId))
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteAttachment_NotFound_ReturnsNotFound() throws Exception {
        Long attachmentId = 999L;
        doThrow(new AttachmentNotFoundException(MessageKey.ATTACHMENT_NOT_FOUND_WITH_ID.getMessage()))
                .when(attachmentService).deleteAttachment(attachmentId);

        mockMvc.perform(delete(API_BASE_PATH + "/{attachmentId}", attachmentId))
                .andExpect(status().isNotFound());
    }
    @Test
    void getTaskAttachments_Success() throws Exception {
        Long taskId = 1L;
        List<FileAttachmentResponse> mockAttachments = Arrays.asList(
                new FileAttachmentResponse(),
                new FileAttachmentResponse()
        );
        when(attachmentService.getTaskAttachments(taskId)).thenReturn(mockAttachments);

        mockMvc.perform(get(API_BASE_PATH + "/task/{taskId}", taskId))
                .andExpect(status().isOk());
    }

    @Test
    void getTaskAttachments_EmptyList_ReturnsOk() throws Exception {
        Long taskId = 1L;
        when(attachmentService.getTaskAttachments(taskId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get(API_BASE_PATH + "/task/{taskId}", taskId))
                .andExpect(status().isOk());
    }
}