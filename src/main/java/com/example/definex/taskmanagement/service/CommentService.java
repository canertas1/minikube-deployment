package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.dto.request.CreateCommentRequest;
import com.example.definex.taskmanagement.dto.request.UpdateCommentRequest;
import com.example.definex.taskmanagement.dto.response.CommentResponse;
import com.example.definex.taskmanagement.dto.response.CreatedCommentResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedCommentResponse;

public interface CommentService {
    CreatedCommentResponse save(CreateCommentRequest createCommentRequest);
    UpdatedCommentResponse update(UpdateCommentRequest updateCommentRequest, Long id);
    void delete(Long id);
    CommentResponse findById(Long id);
}
