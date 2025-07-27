package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.authorization.impl.AttachmentAuthorizationImpl;
import com.example.definex.taskmanagement.dto.mapper.AttachmentMapper;
import com.example.definex.taskmanagement.dto.request.UploadFileAttachmentRequest;
import com.example.definex.taskmanagement.dto.response.FileAttachmentResponse;
import com.example.definex.taskmanagement.dto.response.UploadedFileAttachmentResponse;
import com.example.definex.taskmanagement.entities.*;
import com.example.definex.taskmanagement.exception.AttachmentNotFoundException;
import com.example.definex.taskmanagement.exception.TaskNotFoundException;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.repository.AttachmentRepository;
import com.example.definex.taskmanagement.repository.TaskRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.impl.AttachmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
 class AttachmentServiceImplTest {
    @Mock
    private AttachmentRepository attachmentRepository;

    @Mock
    private AttachmentMapper attachmentMapper;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttachmentAuthorizationImpl attachmentAuthorizationImpl;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    private User teamLeader;
    private User groupManager;
    private User teamMember;
    private User otherUser;
    private Task task;
    private Attachment attachment;
    private UploadFileAttachmentRequest uploadRequest;
    private UploadedFileAttachmentResponse uploadResponse;
    private FileAttachmentResponse fileAttachmentResponse;
    private Department department;
    private Project project;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(attachmentService, "uploadDir", "uploads");
        ReflectionTestUtils.setField(attachmentService, "maxFileSize", "10MB");

        department = new Department();
        department.setId(1L);
        department.setName("IT Department");

        project = new Project();
        project.setId(1L);
        project.setTitle("Project X");
        project.setDepartment(department);

        teamLeader = new User();
        teamLeader.setId(1L);
        teamLeader.setName("teamleader");
        teamLeader.setRole(Role.TEAM_LEADER);

        groupManager = new User();
        groupManager.setId(2L);
        groupManager.setName("groupmanager");
        groupManager.setRole(Role.GROUP_MANAGER);
        groupManager.setDepartment(department);

        teamMember = new User();
        teamMember.setId(3L);
        teamMember.setName("teammember");
        teamMember.setRole(Role.TEAM_MEMBER);

        otherUser = new User();
        otherUser.setId(4L);
        otherUser.setName("otheruser");
        otherUser.setRole(Role.TEAM_MEMBER);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setProject(project);
        task.setAssignee(teamMember);

        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setFileName("test_file.txt");
        attachment.setFilePath("uploads/test_file.txt");
        attachment.setTask(task);
        attachment.setUser(teamMember);
        attachment.setIsDeleted(false);

        uploadRequest = new UploadFileAttachmentRequest();
        uploadRequest.setTaskId(1L);
        uploadRequest.setUserId(3L);

        uploadResponse = new UploadedFileAttachmentResponse();
        uploadResponse.setFileName("test_file.txt");

        fileAttachmentResponse = new FileAttachmentResponse();
        fileAttachmentResponse.setFileName("test_file.txt");

        when(multipartFile.getOriginalFilename()).thenReturn("test_file.txt");
        when(multipartFile.isEmpty()).thenReturn(false);
        when(multipartFile.getSize()).thenReturn(1024L);
    }

    @Test
    void uploadFile_WhenUserIsAuthorized_ShouldUploadAndReturnResponse() throws IOException {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(Optional.of(teamMember));
        when(attachmentAuthorizationImpl.userCanAttachFileToTask(task)).thenReturn(true);
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));
        when(attachmentRepository.save(any(Attachment.class))).thenReturn(attachment);
        when(attachmentMapper.attachmentToUploadedFileAttachmentResponse(attachment)).thenReturn(uploadResponse);

        UploadedFileAttachmentResponse result = attachmentService.uploadFile(multipartFile, uploadRequest);

        assertNotNull(result);
        assertEquals("test_file.txt", result.getFileName());
        verify(attachmentAuthorizationImpl).userCanAttachFileToTask(task);
        verify(attachmentRepository).save(any(Attachment.class));
    }

    @Test
    void uploadFile_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () ->
                attachmentService.uploadFile(multipartFile, uploadRequest)
        );
        verify(taskRepository).findById(1L);
        verifyNoInteractions(attachmentAuthorizationImpl);
    }
    @Test
    void uploadFile_WhenUserNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                attachmentService.uploadFile(multipartFile, uploadRequest)
        );
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(3L);
        verifyNoInteractions(attachmentAuthorizationImpl);
    }

    @Test
    void uploadFile_WhenUnauthorized_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(Optional.of(teamMember));
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(attachmentAuthorizationImpl).userCanAttachFileToTask(task);

        assertThrows(UnauthorizedAccessException.class, () ->
                attachmentService.uploadFile(multipartFile, uploadRequest)
        );
        verify(taskRepository).findById(1L);
        verify(userRepository).findById(3L);
        verify(attachmentAuthorizationImpl).userCanAttachFileToTask(task);
    }

    @Test
    void uploadFile_WhenFileEmpty_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(Optional.of(teamMember));
        when(attachmentAuthorizationImpl.userCanAttachFileToTask(task)).thenReturn(true);
        when(multipartFile.isEmpty()).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                attachmentService.uploadFile(multipartFile, uploadRequest)
        );
    }

    @Test
    void uploadFile_WhenFileSizeExceedsLimit_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(3L)).thenReturn(Optional.of(teamMember));
        when(attachmentAuthorizationImpl.userCanAttachFileToTask(task)).thenReturn(true);
        when(multipartFile.getSize()).thenReturn(20 * 1024 * 1024L); // 20MB

        assertThrows(IllegalArgumentException.class, () ->
                attachmentService.uploadFile(multipartFile, uploadRequest)
        );
    }

    @Test
    void downloadFile_WhenUserIsAuthorized_ShouldReturnResource() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        when(attachmentAuthorizationImpl.userCanDownloadAttachment(attachment)).thenReturn(true);

        URI mockUri = URI.create("file:///uploads/test_file.txt");

        try (MockedStatic<Paths> pathsMock = mockStatic(Paths.class)) {
            Path path = mock(Path.class);
            pathsMock.when(() -> Paths.get(attachment.getFilePath())).thenReturn(path);
            when(path.toUri()).thenReturn(mockUri);

            UrlResource mockUrlResource = mock(UrlResource.class);
            when(mockUrlResource.exists()).thenReturn(true);

            try (MockedConstruction<UrlResource> mockedConstruction = mockConstruction(UrlResource.class,
                    (mock, context) -> {
                        when(mock.exists()).thenReturn(true);
                    })) {
                Resource result = attachmentService.downloadFile(1L);
                assertNotNull(result);
                verify(attachmentAuthorizationImpl).userCanDownloadAttachment(attachment);
            }
        }
    }

    @Test
    void downloadFile_WhenAttachmentNotFound_ShouldThrowException() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AttachmentNotFoundException.class, () ->
                attachmentService.downloadFile(1L)
        );
        verify(attachmentRepository).findById(1L);
        verifyNoInteractions(attachmentAuthorizationImpl);
    }
    @Test
    void downloadFile_WhenUnauthorized_ShouldThrowException() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(attachmentAuthorizationImpl).userCanDownloadAttachment(attachment);

        assertThrows(UnauthorizedAccessException.class, () ->
                attachmentService.downloadFile(1L)
        );
        verify(attachmentRepository).findById(1L);
        verify(attachmentAuthorizationImpl).userCanDownloadAttachment(attachment);
    }
    @Test
    void deleteAttachment_WhenUserIsAuthorized_ShouldSetIsDeletedTrue() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        when(attachmentAuthorizationImpl.userCanDeleteAttachment(attachment)).thenReturn(true);

        attachmentService.deleteAttachment(1L);

        assertTrue(attachment.getIsDeleted());
        verify(attachmentRepository).save(attachment);
        verify(attachmentAuthorizationImpl).userCanDeleteAttachment(attachment);
    }

    @Test
    void deleteAttachment_WhenAttachmentNotFound_ShouldThrowException() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AttachmentNotFoundException.class, () ->
                attachmentService.deleteAttachment(1L)
        );
        verify(attachmentRepository).findById(1L);
        verifyNoInteractions(attachmentAuthorizationImpl);
    }

    @Test
    void deleteAttachment_WhenUnauthorized_ShouldThrowException() {
        when(attachmentRepository.findById(1L)).thenReturn(Optional.of(attachment));
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(attachmentAuthorizationImpl).userCanDeleteAttachment(attachment);

        assertThrows(UnauthorizedAccessException.class, () ->
                attachmentService.deleteAttachment(1L)
        );
        verify(attachmentRepository).findById(1L);
        verify(attachmentAuthorizationImpl).userCanDeleteAttachment(attachment);
    }

    @Test
    void getTaskAttachments_WhenUserIsAuthorized_ShouldReturnAttachments() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(attachmentAuthorizationImpl.userCanViewTaskAttachments(task)).thenReturn(true);
        when(attachmentRepository.findByTask_Id(1L)).thenReturn(Arrays.asList(attachment));
        when(attachmentMapper.attachmentToFileAttachmentResponse(attachment)).thenReturn(fileAttachmentResponse);

        List<FileAttachmentResponse> result = attachmentService.getTaskAttachments(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(taskRepository).findById(1L);
        verify(attachmentAuthorizationImpl).userCanViewTaskAttachments(task);
        verify(attachmentRepository).findByTask_Id(1L);
    }

    @Test
    void getTaskAttachments_WhenTaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () ->
                attachmentService.getTaskAttachments(1L)
        );
        verify(taskRepository).findById(1L);
        verifyNoInteractions(attachmentAuthorizationImpl);
    }

    @Test
    void getTaskAttachments_WhenUnauthorized_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        doThrow(new UnauthorizedAccessException("Unauthorized")).when(attachmentAuthorizationImpl).userCanViewTaskAttachments(task);

        assertThrows(UnauthorizedAccessException.class, () ->
                attachmentService.getTaskAttachments(1L)
        );
        verify(taskRepository).findById(1L);
        verify(attachmentAuthorizationImpl).userCanViewTaskAttachments(task);
    }

    @Test
    void parseMaxFileSize_WithMBUnit_ShouldReturnCorrectByteSize() {
        long result = (long) ReflectionTestUtils.invokeMethod(attachmentService, "parseMaxFileSize", "5MB");
        assertEquals(5 * 1024 * 1024, result);
    }
    @Test
    void parseMaxFileSize_WithInvalidFormat_ShouldReturnDefaultSize() {
        long result = (long) ReflectionTestUtils.invokeMethod(attachmentService, "parseMaxFileSize", "invalid");
        assertEquals(10 * 1024 * 1024, result);
    }
}