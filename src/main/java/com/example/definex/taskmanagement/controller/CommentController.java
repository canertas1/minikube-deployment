package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateCommentRequest;
import com.example.definex.taskmanagement.dto.request.UpdateCommentRequest;
import com.example.definex.taskmanagement.dto.response.CommentResponse;
import com.example.definex.taskmanagement.dto.response.CreatedCommentResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedCommentResponse;
import com.example.definex.taskmanagement.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    @PostMapping
    public ResponseEntity<CreatedCommentResponse> createComment(@Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(commentService.save(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UpdatedCommentResponse> updateComment(
            @PathVariable Long id,
            @RequestBody UpdateCommentRequest request) {
        return ResponseEntity.ok(commentService.update(request, id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(commentService.findById(id));
    }
}