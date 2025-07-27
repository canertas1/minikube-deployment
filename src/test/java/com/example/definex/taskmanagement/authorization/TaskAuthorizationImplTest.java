package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.authorization.impl.TaskAuthorizationImpl;
import com.example.definex.taskmanagement.entities.*;
import com.example.definex.taskmanagement.exception.InvalidTaskStateTransitionException;
import com.example.definex.taskmanagement.exception.TaskCompletedException;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import com.example.definex.taskmanagement.exception.constants.MessageKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
 class TaskAuthorizationImplTest {
    @InjectMocks
    private TaskAuthorizationImpl taskAuthorizationImpl;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User teamLeader;
    private User groupManager;
    private User regularUser;
    private User assignedUser;
    private Department department;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        department = new Department();
        department.setId(1L);
        department.setName("Test Department");

        Department otherDepartment = new Department();
        otherDepartment.setId(2L);
        otherDepartment.setName("Other Department");

        teamLeader = new User();
        teamLeader.setId(1L);
        teamLeader.setName("teamleader");
        teamLeader.setRole(Role.TEAM_LEADER);
        teamLeader.setDepartment(department);

        groupManager = new User();
        groupManager.setId(2L);
        groupManager.setName("groupmanager");
        groupManager.setRole(Role.GROUP_MANAGER);
        groupManager.setDepartment(department);

        regularUser = new User();
        regularUser.setId(3L);
        regularUser.setName("team_member");
        regularUser.setRole(Role.TEAM_MEMBER);
        regularUser.setDepartment(department);

        assignedUser = new User();
        assignedUser.setId(4L);
        assignedUser.setName("assigneduser");
        assignedUser.setRole(Role.TEAM_MEMBER);
        assignedUser.setDepartment(department);

        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setDepartment(department);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setState(TaskStateType.BACKLOG);
        task.setProject(project);
        task.setAssignee(assignedUser);
    }

    @Test
    void canCreateTask_RegularUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.canCreateTask(project));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CREATE_TASK.toString(),
                exception.getMessage());
    }

    @Test
    void canAccessTask_TeamLeader_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = taskAuthorizationImpl.canAccessTask(task);

        assertTrue(result);
    }

    @Test
    void canAccessTask_GroupManager_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = taskAuthorizationImpl.canAccessTask(task);

        assertTrue(result);
    }

    @Test
    void canAccessTask_AssignedUser_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(assignedUser);

        boolean result = taskAuthorizationImpl.canAccessTask(task);

        assertTrue(result);
    }

    @Test
    void canAccessTask_RegularUser_ReturnsFalse() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        boolean result = taskAuthorizationImpl.canAccessTask(task);

        assertFalse(result);
    }

    @Test
    void validateTaskAccess_AuthorizedUser_NoException() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        assertDoesNotThrow(() -> taskAuthorizationImpl.validateTaskAccess(task));
    }

    @Test
    void validateTaskAccess_UnauthorizedUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.validateTaskAccess(task));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_ACCESS_TO_TASK.toString(),
                exception.getMessage());
    }

    @Test
    void canManageTask_TeamLeader_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = taskAuthorizationImpl.canManageTask(task);

        assertTrue(result);
    }

    @Test
    void canManageTask_GroupManager_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = taskAuthorizationImpl.canManageTask(task);

        assertTrue(result);
    }

    @Test
    void canManageTask_RegularUser_ReturnsFalse() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        boolean result = taskAuthorizationImpl.canManageTask(task);

        assertFalse(result);
    }

    @Test
    void validateTaskManagement_AuthorizedUser_NoException() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        assertDoesNotThrow(() -> taskAuthorizationImpl.validateTaskManagement(task));
    }

    @Test
    void validateTaskManagement_UnauthorizedUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.validateTaskManagement(task));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_MANAGE_TASK.toString(),
                exception.getMessage());
    }

    @Test
    void validateTaskStateChange_ValidTransition_NoException() {
        task.setState(TaskStateType.BACKLOG);
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        assertDoesNotThrow(() ->
                taskAuthorizationImpl.validateTaskStateChange(task, TaskStateType.IN_PROGRESS));
    }

    @Test
    void validateTaskStateChange_CompletedTask_ThrowsException() {
        task.setState(TaskStateType.COMPLETED);
        when(authentication.getPrincipal()).thenReturn(teamLeader);

          assertThrows(TaskCompletedException.class, () ->
                taskAuthorizationImpl.validateTaskStateChange(task, TaskStateType.IN_PROGRESS));
    }

    @Test
    void validateTaskStateChange_InvalidTransition_ThrowsException() {
        task.setState(TaskStateType.BACKLOG);
        when(authentication.getPrincipal()).thenReturn(teamLeader);

             assertThrows(InvalidTaskStateTransitionException.class, () ->
                taskAuthorizationImpl.validateTaskStateChange(task, TaskStateType.BLOCKED));
    }

    @Test
    void canAssignTask_TeamLeader_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = taskAuthorizationImpl.canAssignTask(task);

        assertTrue(result);
    }

    @Test
    void canAssignTask_GroupManager_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = taskAuthorizationImpl.canAssignTask(task);

        assertTrue(result);
    }

    @Test
    void canAssignTask_RegularUser_ReturnsFalse() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        boolean result = taskAuthorizationImpl.canAssignTask(task);

        assertFalse(result);
    }

    @Test
    void validateTaskAssignment_AuthorizedUser_NoException() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        assertDoesNotThrow(() -> taskAuthorizationImpl.validateTaskAssignment(task));
    }

    @Test
    void validateTaskAssignment_UnauthorizedUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.validateTaskAssignment(task));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_ASSIGN_TASK.toString(),
                exception.getMessage());
    }

    @Test
    void canChangeTaskPriority_TeamLeader_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = taskAuthorizationImpl.canChangeTaskPriority(task);

        assertTrue(result);
    }

    @Test
    void canChangeTaskPriority_GroupManager_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = taskAuthorizationImpl.canChangeTaskPriority(task);

        assertTrue(result);
    }

    @Test
    void canChangeTaskPriority_RegularUser_ReturnsFalse() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        boolean result = taskAuthorizationImpl.canChangeTaskPriority(task);

        assertFalse(result);
    }

    @Test
    void validateTaskPriorityChange_AuthorizedUser_NoException() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        assertDoesNotThrow(() -> taskAuthorizationImpl.validateTaskPriorityChange(task));
    }

    @Test
    void validateTaskPriorityChange_UnauthorizedUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.validateTaskPriorityChange(task));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_CHANGE_TASK_PRIORITY.toString(),
                exception.getMessage());
    }

    @Test
    void canDeleteTask_TeamLeader_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = taskAuthorizationImpl.canDeleteTask(task);

        assertTrue(result);
    }

    @Test
    void canDeleteTask_GroupManager_ReturnsTrue() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = taskAuthorizationImpl.canDeleteTask(task);

        assertTrue(result);
    }

    @Test
    void canDeleteTask_RegularUser_ReturnsFalse() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        boolean result = taskAuthorizationImpl.canDeleteTask(task);

        assertFalse(result);
    }

    @Test
    void validateTaskDeletion_AuthorizedUser_NoException() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        assertDoesNotThrow(() -> taskAuthorizationImpl.validateTaskDeletion(task));
    }

    @Test
    void validateTaskDeletion_UnauthorizedUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.validateTaskDeletion(task));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_PERMISSION_TO_DELETE_TASK.toString(),
                exception.getMessage());
    }

    @Test
    void validateTaskStateChange_UnauthorizedUser_ThrowsException() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        Exception exception = assertThrows(UnauthorizedAccessException.class,
                () -> taskAuthorizationImpl.validateTaskStateChange(task, TaskStateType.IN_PROGRESS));

        assertEquals(MessageKey.USER_DOES_NOT_HAVE_ACCESS_TO_TASK.getMessage(),
                exception.getMessage());
    }
}