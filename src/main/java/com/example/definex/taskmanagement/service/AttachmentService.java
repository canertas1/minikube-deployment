package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.UploadFileAttachmentRequest;
import com.example.definex.taskmanagement.dto.response.FileAttachmentResponse;
import com.example.definex.taskmanagement.dto.response.UploadedFileAttachmentResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttachmentService {
    UploadedFileAttachmentResponse uploadFile(MultipartFile file, UploadFileAttachmentRequest uploadFileAttachmentRequest);
    Resource downloadFile(Long attachmentId);
    void deleteAttachment(Long attachmentId);
    List<FileAttachmentResponse> getTaskAttachments(Long taskId);
}
