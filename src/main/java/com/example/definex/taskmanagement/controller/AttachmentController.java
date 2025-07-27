package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.response.FileAttachmentResponse;
import com.example.definex.taskmanagement.dto.request.UploadFileAttachmentRequest;
import com.example.definex.taskmanagement.dto.response.UploadedFileAttachmentResponse;
import com.example.definex.taskmanagement.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping("/upload")
    public ResponseEntity<UploadedFileAttachmentResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam("taskId") Long taskId) {

        UploadFileAttachmentRequest request = new UploadFileAttachmentRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);

        UploadedFileAttachmentResponse response = attachmentService.uploadFile(file, request);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) {
        Resource resource = attachmentService.downloadFile(attachmentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    @DeleteMapping("/{attachmentId}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(attachmentId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<FileAttachmentResponse>> getTaskAttachments(@PathVariable Long taskId) {
        return ResponseEntity.ok(attachmentService.getTaskAttachments(taskId));
    }
}