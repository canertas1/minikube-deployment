package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.authorization.AttachmentAuthorization;
import com.example.definex.taskmanagement.dto.mapper.AttachmentMapper;
import com.example.definex.taskmanagement.dto.request.UploadFileAttachmentRequest;
import com.example.definex.taskmanagement.dto.response.FileAttachmentResponse;
import com.example.definex.taskmanagement.dto.response.UploadedFileAttachmentResponse;
import com.example.definex.taskmanagement.entities.Attachment;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.AttachmentNotFoundException;
import com.example.definex.taskmanagement.exception.TaskNotFoundException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.AttachmentRepository;
import com.example.definex.taskmanagement.repository.TaskRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.AttachmentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final AttachmentMapper attachmentMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final AttachmentAuthorization attachmentAuthorization;
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Override
    public UploadedFileAttachmentResponse uploadFile(MultipartFile file, UploadFileAttachmentRequest uploadFileAttachmentRequest) {

        Long userId = uploadFileAttachmentRequest.getUserId();
        Long taskId = uploadFileAttachmentRequest.getTaskId();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        User user = userRepository.findById(userId)
                        .orElseThrow(()->new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.toString()+userId));

        attachmentAuthorization.userCanAttachFileToTask(task);
        validateFile(file);

        String storedFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = saveToDisk(file, storedFileName);

        Attachment attachment = new Attachment();
        attachment.setTask(task);
        attachment.setFileName(storedFileName);
        attachment.setFilePath(filePath.toString());
        attachment.setUser(user);

        Attachment savedAttachment = attachmentRepository.save(attachment);

        return attachmentMapper.attachmentToUploadedFileAttachmentResponse(savedAttachment);
    }
    @Override
    public Resource downloadFile(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(MessageKey.ATTACHMENT_NOT_FOUND_WITH_ID.toString()+attachmentId));

        attachmentAuthorization.userCanDownloadAttachment(attachment);

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new EntityNotFoundException(MessageKey.ATTACHMENT_NOT_FOUND_WITH_FILE_NAME + attachment.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException(MessageKey.FILE_ACCESS_ERROR.toString(),ex);
        }
    }
    @Override
    public void deleteAttachment(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new AttachmentNotFoundException(MessageKey.ATTACHMENT_NOT_FOUND_WITH_ID.toString()+attachmentId));

        attachmentAuthorization.userCanDeleteAttachment(attachment);

        attachment.setIsDeleted(true);
        attachmentRepository.save(attachment);
    }
    @Override
    public List<FileAttachmentResponse> getTaskAttachments(Long taskId){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()+taskId));

        attachmentAuthorization.userCanViewTaskAttachments(task);

        List<Attachment> attachments = attachmentRepository.findByTask_Id(taskId);

        return attachments.stream().map(attachmentMapper::attachmentToFileAttachmentResponse).collect(Collectors.toList());
    }
    private Path saveToDisk(MultipartFile file, String storedFileName) {
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(storedFileName);

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath;
        } catch (IOException e) {
            throw new RuntimeException(MessageKey.FILE_UPLOAD_ERROR.toString(),e);
        }
    }
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException(MessageKey.FILE_CANNOT_BE_EMPTY.toString());

        long maxFileSizeBytes = parseMaxFileSize(maxFileSize);

        if (file.getSize() > maxFileSizeBytes)
            throw new IllegalArgumentException(MessageKey.FILE_SIZE_EXCEEDS_LIMIT.toString());
    }
    private long parseMaxFileSize(String maxFileSize) {
        if (maxFileSize.endsWith("MB")) {
            String valueStr = maxFileSize.substring(0, maxFileSize.length() - 2);
            long mbValue = Long.parseLong(valueStr);
            return mbValue * (1024L * 1024L);
        }
        return 10L * (1024L * 1024L);
    }
}