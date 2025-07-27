package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.authorization.impl.ProjectAuthorizationImpl;
import com.example.definex.taskmanagement.entities.Department;
import com.example.definex.taskmanagement.entities.Project;
import com.example.definex.taskmanagement.entities.Role;
import com.example.definex.taskmanagement.entities.User;
import com.example.definex.taskmanagement.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class ProjectAuthorizationImplTest {

    @InjectMocks
    private ProjectAuthorizationImpl projectAuthorizationImpl;

    private Authentication authentication;
    private SecurityContext securityContext;
    private User user;
    private Department department;
    private Project project;

    @BeforeEach
     void setup() {
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        user = mock(User.class);
        department = mock(Department.class);
        project = mock(Project.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(project.getDepartment()).thenReturn(department);
    }
    @Test
     void shouldAuthorizeTeamLeader() {

        when(user.getRole()).thenReturn(Role.TEAM_LEADER);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertDoesNotThrow(() -> projectAuthorizationImpl.userHasAuthorization(project));
        }
    }
    @Test
     void shouldAuthorizeGroupManagerOfSameDepartment() {
        when(user.getRole()).thenReturn(Role.GROUP_MANAGER);
        when(department.getId()).thenReturn(1L);

        Department userDepartment = mock(Department.class);
        when(userDepartment.getId()).thenReturn(1L);
        when(user.getDepartment()).thenReturn(userDepartment);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertDoesNotThrow(() -> projectAuthorizationImpl.userHasAuthorization(project));
        }
    }
    @Test
     void shouldNotAuthorizeGroupManagerOfDifferentDepartment() {
        when(user.getRole()).thenReturn(Role.GROUP_MANAGER);
        when(department.getId()).thenReturn(1L);

        Department userDepartment = mock(Department.class);
        when(userDepartment.getId()).thenReturn(2L);
        when(user.getDepartment()).thenReturn(userDepartment);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(UnauthorizedAccessException.class, () -> projectAuthorizationImpl.userHasAuthorization(project));
        }
    }
    @Test
     void shouldNotAuthorizeRegularUser() {
        when(user.getRole()).thenReturn(Role.TEAM_MEMBER);

        try (MockedStatic<SecurityContextHolder> securityContextHolder = mockStatic(SecurityContextHolder.class)) {
            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            assertThrows(UnauthorizedAccessException.class, () -> projectAuthorizationImpl.userHasAuthorization(project));
        }
    }
}