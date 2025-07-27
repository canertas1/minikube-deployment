package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.authorization.impl.CommentAuthorizationImpl;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.Project;
import com.example.definex.taskmanagement.entities.Role;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class CommentAuthorizationImplTest {

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private CommentAuthorizationImpl commentAuthorizationImpl;

    private Department department1;
    private Department department2;
    private Project project;
    private Task task;
    private User regularUser;
    private User groupManager;
    private User adminUser;

    @BeforeEach
    public void setup() {
        department1 = new Department();
        department1.setId(1L);
        department1.setName("Department 1");

        department2 = new Department();
        department2.setId(2L);
        department2.setName("Department 2");

        project = new Project();
        project.setId(1L);
        project.setTitle("Test Project");
        project.setDepartment(department1);

        task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        task.setProject(project);

        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setName("regular");
        regularUser.setRole(Role.TEAM_MEMBER);
        regularUser.setDepartment(department1);

        groupManager = new User();
        groupManager.setId(2L);
        groupManager.setName("manager");
        groupManager.setRole(Role.GROUP_MANAGER);
        groupManager.setDepartment(department1);


        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
     void testRegularUserCannotCommentOnUnassignedTask() {
        when(authentication.getPrincipal()).thenReturn(regularUser);
        task.setAssignee(null);

        assertThrows(UnauthorizedAccessException.class, () -> commentAuthorizationImpl.userCanReachComment(task));
    }

    @Test
     void testRegularUserCanCommentOnAssignedTask() {
        when(authentication.getPrincipal()).thenReturn(regularUser);
        task.setAssignee(regularUser);

        assertDoesNotThrow(() -> commentAuthorizationImpl.userCanReachComment(task));
    }

    @Test
     void testGroupManagerCanCommentOnOwnDepartmentTask() {
        when(authentication.getPrincipal()).thenReturn(groupManager);
        task.setAssignee(groupManager);

        task.getProject().setDepartment(department1);
        groupManager.setDepartment(department1);

        assertDoesNotThrow(() -> commentAuthorizationImpl.userCanReachComment(task));
    }

    @Test
     void testGroupManagerCannotCommentOnOtherDepartmentTask() {
        when(authentication.getPrincipal()).thenReturn(groupManager);
        task.setAssignee(groupManager);

        task.getProject().setDepartment(department2);
        groupManager.setDepartment(department1);

        assertThrows(UnauthorizedAccessException.class, () -> commentAuthorizationImpl.userCanReachComment(task));
    }

    @Test
     void testUserCannotCommentOnTaskAssignedToOtherUser() {
        when(authentication.getPrincipal()).thenReturn(regularUser);

        User otherUser = new User();
        otherUser.setId(4L);
        task.setAssignee(otherUser);

        assertThrows(UnauthorizedAccessException.class, () -> commentAuthorizationImpl.userCanReachComment(task));
    }
}