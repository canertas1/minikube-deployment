package com.example.definex.taskmanagement.service;

import com.example.definex.taskmanagement.authorization.impl.CommentAuthorizationImpl;
import com.example.definex.taskmanagement.dto.mapper.CommentMapper;
import com.example.definex.taskmanagement.dto.request.CreateCommentRequest;
import com.example.definex.taskmanagement.dto.request.UpdateCommentRequest;
import com.example.definex.taskmanagement.dto.response.CommentResponse;
import com.example.definex.taskmanagement.dto.response.CreatedCommentResponse;
import com.example.definex.taskmanagement.dto.response.UpdatedCommentResponse;
import com.example.definex.taskmanagement.entities.*;
import com.example.definex.taskmanagement.exception.CommentNotFoundException;
import com.example.definex.taskmanagement.exception.TaskNotFoundException;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import com.example.definex.taskmanagement.exception.UserNotFoundException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import com.example.definex.taskmanagement.repository.CommentRepository;
import com.example.definex.taskmanagement.repository.TaskRepository;
import com.example.definex.taskmanagement.repository.UserRepository;
import com.example.definex.taskmanagement.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
 class CommentServiceImplTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CommentAuthorizationImpl commentAuthorizationImpl;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User loggedInUser;
    private Task task;
    private Comment comment;
    private Department department;
    private Project project;
    private final Long userId = 1L;
    private final Long taskId = 1L;
    private final Long commentId = 1L;
    private final Long departmentId = 1L;
    private final Long projectId = 1L;

    @BeforeEach
    void setup() {

        department = new Department();
        department.setId(departmentId);
        department.setName("Test Department");

        project = new Project();
        project.setId(projectId);
        project.setTitle("Test Project");
        project.setDepartment(department);

        loggedInUser = new User();
        loggedInUser.setId(userId);
        loggedInUser.setName("testUser");
        loggedInUser.setDepartment(department);

        task = new Task();
        task.setId(taskId);
        task.setTitle("Test Task");
        task.setAssignee(loggedInUser);
        task.setProject(project);

        comment = new Comment();
        comment.setId(commentId);
        comment.setContent("Test Comment");
        comment.setUser(loggedInUser);
        comment.setTask(task);
        comment.setIsDeleted(false);
    }


    @Test
    void save_WhenUserIsAssigneeAndAuthorized_ShouldSucceed() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);
        request.setContent("Test Comment");

        CreatedCommentResponse expected = new CreatedCommentResponse();
        expected.setContent("Test Comment");

        when(userRepository.findById(userId)).thenReturn(Optional.of(loggedInUser));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(commentMapper.createCommentRequestToComment(request)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCreatedCommentResponse(comment)).thenReturn(expected);

        CreatedCommentResponse result = commentService.save(request);

        assertThat(result).isEqualTo(expected);
        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verify(commentMapper).createCommentRequestToComment(request);
        verify(commentRepository).save(comment);
        verify(commentMapper).commentToCreatedCommentResponse(comment);
    }

    @Test
    void save_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.save(request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(MessageKey.USER_NOT_FOUND_WITH_ID.toString());

        verify(userRepository).findById(userId);
        verifyNoInteractions(taskRepository);
        verifyNoInteractions(commentAuthorizationImpl);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void save_WhenTaskNotFound_ShouldThrowTaskNotFoundException() {
        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(loggedInUser));
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.save(request))
                .isInstanceOf(TaskNotFoundException.class)
                .hasMessageContaining(MessageKey.TASK_NOT_FOUND_WITH_ID.toString());

        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verifyNoInteractions(commentAuthorizationImpl);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void save_WhenUserUnauthorized_ShouldThrowUnauthorizedAccessException() {

        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId(userId);
        request.setTaskId(taskId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(loggedInUser));
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString()))
                .when(commentAuthorizationImpl).userCanReachComment(task);

        assertThatThrownBy(() -> commentService.save(request))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString());

        verify(userRepository).findById(userId);
        verify(taskRepository).findById(taskId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(commentRepository);
    }

    @Test
    void update_WhenCommentExistsAndUserAuthorized_ShouldSucceed() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated Comment");

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setContent("Updated Comment");
        updatedComment.setUser(loggedInUser);
        updatedComment.setTask(task);

        UpdatedCommentResponse expected = new UpdatedCommentResponse();
        expected.setContent("Updated Comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentAuthorizationImpl).userCanReachComment(task);
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);
        when(commentMapper.commentToUpdatedCommentResponse(updatedComment)).thenReturn(expected);

        UpdatedCommentResponse result = commentService.update(request, commentId);

        assertThat(result).isEqualTo(expected);
        verify(commentRepository).findById(commentId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).commentToUpdatedCommentResponse(any(Comment.class));
    }

    @Test
    void update_WhenCommentNotFound_ShouldThrowCommentNotFoundException() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated Comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.update(request, commentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining(MessageKey.COMMENT_NOT_FOUND_WITH_ID.toString());

        verify(commentRepository).findById(commentId);
        verifyNoInteractions(commentAuthorizationImpl);
        verify(commentRepository, never()).save(any(Comment.class));
        verifyNoInteractions(commentMapper);
    }

    @Test
    void update_WhenUserUnauthorized_ShouldThrowUnauthorizedAccessException() {
        UpdateCommentRequest request = new UpdateCommentRequest();
        request.setContent("Updated Comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString()))
                .when(commentAuthorizationImpl).userCanReachComment(task);

        assertThatThrownBy(() -> commentService.update(request, commentId))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString());

        verify(commentRepository).findById(commentId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verify(commentRepository, never()).save(any(Comment.class));
        verifyNoInteractions(commentMapper);
    }


    @Test
    void delete_WhenCommentExistsAndUserAuthorized_ShouldMarkAsDeleted() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentAuthorizationImpl).userCanReachComment(task);

        commentService.delete(commentId);

        assertThat(comment.getIsDeleted()).isTrue();
        verify(commentRepository).findById(commentId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verify(commentRepository).save(comment);
    }

    @Test
    void delete_WhenCommentNotFound_ShouldThrowCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.delete(commentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining(MessageKey.COMMENT_NOT_FOUND_WITH_ID.toString());

        verify(commentRepository).findById(commentId);
        verifyNoInteractions(commentAuthorizationImpl);
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void delete_WhenUserUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString()))
                .when(commentAuthorizationImpl).userCanReachComment(task);

        assertThatThrownBy(() -> commentService.delete(commentId))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString());

        verify(commentRepository).findById(commentId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    void findById_WhenCommentExistsAndUserAuthorized_ShouldReturnComment() {
        CommentResponse expected = new CommentResponse();
        expected.setContent("Test Comment");

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doNothing().when(commentAuthorizationImpl).userCanReachComment(task);
        when(commentMapper.commentToCommentResponse(comment)).thenReturn(expected);

        CommentResponse result = commentService.findById(commentId);

        assertThat(result).isEqualTo(expected);
        verify(commentRepository).findById(commentId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verify(commentMapper).commentToCommentResponse(comment);
    }

    @Test
    void findById_WhenCommentNotFound_ShouldThrowCommentNotFoundException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentService.findById(commentId))
                .isInstanceOf(CommentNotFoundException.class)
                .hasMessageContaining(MessageKey.COMMENT_NOT_FOUND_WITH_ID.toString());

        verify(commentRepository).findById(commentId);
        verifyNoInteractions(commentAuthorizationImpl);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void findById_WhenUserUnauthorized_ShouldThrowUnauthorizedAccessException() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        doThrow(new UnauthorizedAccessException(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString()))
                .when(commentAuthorizationImpl).userCanReachComment(task);

        assertThatThrownBy(() -> commentService.findById(commentId))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining(MessageKey.USER_CAN_NOT_COMMENT_TO_UNASSIGNED_TASK.toString());

        verify(commentRepository).findById(commentId);
        verify(commentAuthorizationImpl).userCanReachComment(task);
        verifyNoInteractions(commentMapper);
    }

    @Test
    void save_WhenGroupManagerTriesToCommentOnOtherDepartment_ShouldThrowUnauthorizedAccessException() {
        loggedInUser.setRole(Role.GROUP_MANAGER);

        Department otherDepartment = new Department();
        otherDepartment.setId(2L);
        otherDepartment.setName("Other Department");

        Project otherProject = new Project();
        otherProject.setId(2L);
        otherProject.setDepartment(otherDepartment);

        Task otherDepartmentTask = new Task();
        otherDepartmentTask.setId(2L);
        otherDepartmentTask.setProject(otherProject);
        otherDepartmentTask.setAssignee(loggedInUser);

        CreateCommentRequest request = new CreateCommentRequest();
        request.setUserId(userId);
        request.setTaskId(2L);
        request.setContent("Test Comment");

        when(userRepository.findById(userId)).thenReturn(Optional.of(loggedInUser));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(otherDepartmentTask));
        doThrow(new UnauthorizedAccessException(MessageKey.GROUP_MANAGER_CANNOT_COMMENT_ON_OTHER_DEPARTMENTS.toString()))
                .when(commentAuthorizationImpl).userCanReachComment(otherDepartmentTask);

        assertThatThrownBy(() -> commentService.save(request))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining(MessageKey.GROUP_MANAGER_CANNOT_COMMENT_ON_OTHER_DEPARTMENTS.toString());

        verify(userRepository).findById(userId);
        verify(taskRepository).findById(2L);
        verify(commentAuthorizationImpl).userCanReachComment(otherDepartmentTask);
        verifyNoInteractions(commentMapper);
        verifyNoInteractions(commentRepository);
    }
}
