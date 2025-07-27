package com.example.definex.taskmanagement.service.impl;

import com.example.definex.taskmanagement.authorization.CommentAuthorization;
import com.example.definex.taskmanagement.dto.mapper.CommentMapper;
import com.example.definex.taskmanagement.dto.request.CreateCommentRequest;
import com.example.definex.taskmanagement.dto.request.UpdateCommentRequest;
import com.example.definex.taskmanagement.dto.response.CommentResponse;
import com.example.definex.taskmanagement.dto.response.CreatedCommentResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedCommentResponse;
import com.example.definex.taskmanagement.entities.Comment;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.CommentNotFoundException;
import com.example.definex.taskmanagement.exception.TaskNotFoundException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.CommentRepository;
import com.example.definex.taskmanagement.repository.TaskRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentAuthorization commentAuthorization;

    @Override
    public CreatedCommentResponse save(CreateCommentRequest createCommentRequest){

        Long userId = createCommentRequest.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new UserNotFoundException(MessageKey.USER_NOT_FOUND_WITH_ID.toString()));

        Long taskId = createCommentRequest.getTaskId();
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException(MessageKey.TASK_NOT_FOUND_WITH_ID.toString()));

        commentAuthorization.userCanReachComment(task);

        Comment comment = commentMapper.createCommentRequestToComment(createCommentRequest);
        comment.setUser(user);
        comment.setTask(task);
        Comment savedComment = commentRepository.save(comment);

        return commentMapper.commentToCreatedCommentResponse(savedComment);
    }
    @Override
    public UpdatedCommentResponse update(UpdateCommentRequest updateCommentRequest,Long id){

        Comment comment = commentRepository.findById(id).orElseThrow(()-> new CommentNotFoundException(MessageKey.COMMENT_NOT_FOUND_WITH_ID.toString()));

        commentAuthorization.userCanReachComment(comment.getTask());

        comment.setContent(updateCommentRequest.getContent());

        Comment savedComment = commentRepository.save(comment);
        return commentMapper.commentToUpdatedCommentResponse(savedComment);
    }
    @Override
    public void delete(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new CommentNotFoundException(MessageKey.COMMENT_NOT_FOUND_WITH_ID.toString()));

        commentAuthorization.userCanReachComment(comment.getTask());

        comment.setIsDeleted(true);
        commentRepository.save(comment);
    }
    @Override
    public CommentResponse findById(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(()-> new CommentNotFoundException(MessageKey.COMMENT_NOT_FOUND_WITH_ID.toString()));

        commentAuthorization.userCanReachComment(comment.getTask());

        return commentMapper.commentToCommentResponse(comment);
    }
}