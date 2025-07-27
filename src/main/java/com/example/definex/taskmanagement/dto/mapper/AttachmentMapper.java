package com.example.definex.taskmanagement.dto.mapper;

import com.example.definex.taskmanagement.dto.response.FileAttachmentResponse;
import com.example.definex.taskmanagement.dto.request.UploadFileAttachmentRequest;
import com.example.definex.taskmanagement.dto.response.UploadedFileAttachmentResponse;
import com.example.definex.taskmanagement.entities.Attachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentMapper INSTANCE = Mappers.getMapper(AttachmentMapper.class);

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    UploadedFileAttachmentResponse attachmentToUploadedFileAttachmentResponse(Attachment attachment);

    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    FileAttachmentResponse attachmentToFileAttachmentResponse(Attachment attachment);
}
