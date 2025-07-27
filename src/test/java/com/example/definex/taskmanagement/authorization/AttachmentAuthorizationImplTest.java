package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.authorization.impl.AttachmentAuthorizationImpl;
import com.example.definex.taskmanagement.entities.*;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
 class AttachmentAuthorizationImplTest {
    @InjectMocks
    private AttachmentAuthorizationImpl attachmentAuthorizationImpl;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    private User teamLeader;
    private User groupManager;
    private User teamMember;
    private User otherTeamMember;
    private Department department1;
    private Department department2;
    private Project project;
    private Task task;
    private Attachment attachment;

    @BeforeEach
    void setUp() {
        department1 = new Department();
        department1.setId(1L);
        department1.setName("Departman 1");

        department2 = new Department();
        department2.setId(2L);
        department2.setName("Departman 2");

        teamLeader = new User();
        teamLeader.setId(1L);
        teamLeader.setRole(Role.TEAM_LEADER);
        teamLeader.setDepartment(department1);

        groupManager = new User();
        groupManager.setId(2L);
        groupManager.setRole(Role.GROUP_MANAGER);
        groupManager.setDepartment(department1);

        teamMember = new User();
        teamMember.setId(3L);
        teamMember.setRole(Role.TEAM_MEMBER);
        teamMember.setDepartment(department1);

        otherTeamMember = new User();
        otherTeamMember.setId(4L);
        otherTeamMember.setRole(Role.TEAM_MEMBER);
        otherTeamMember.setDepartment(department1);


        project = new Project();
        project.setId(1L);
        project.setTitle("Proje 1");
        project.setDepartment(department1);

        task = new Task();
        task.setId(1L);
        task.setTitle("Görev 1");
        task.setProject(project);
        task.setAssignee(teamMember);

        attachment = new Attachment();
        attachment.setId(1L);
        attachment.setTask(task);
        attachment.setUser(teamMember);


        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }



    @Test
    void teamLeaderCanAttachFileToTask() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = attachmentAuthorizationImpl.userCanAttachFileToTask(task);

        assertTrue(result);
    }

    @Test
    void groupManagerCanAttachFileToTaskInSameDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = attachmentAuthorizationImpl.userCanAttachFileToTask(task);

        assertTrue(result);
    }

    @Test
    void groupManagerCannotAttachFileToTaskInDifferentDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        Project differentProject = new Project();
        differentProject.setId(2L);
        differentProject.setDepartment(department2);
        task.setProject(differentProject);

        boolean result = attachmentAuthorizationImpl.userCanAttachFileToTask(task);

        assertFalse(result);
    }

    @Test
    void teamMemberCanAttachFileToAssignedTask() {
        when(authentication.getPrincipal()).thenReturn(teamMember);

        boolean result = attachmentAuthorizationImpl.userCanAttachFileToTask(task);

        assertTrue(result);
    }

    @Test
    void teamMemberCannotAttachFileToUnassignedTask() {
        when(authentication.getPrincipal()).thenReturn(otherTeamMember);

        Exception exception = assertThrows(UnauthorizedAccessException.class, () -> {
            attachmentAuthorizationImpl.userCanAttachFileToTask(task);
        });

        assertTrue(exception.getMessage().contains("USER_CANNOT_ATTACH_FILES_TO_UNASSIGNED_TASKS"));
    }

    @Test
    void teamLeaderCanDownloadAttachment() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = attachmentAuthorizationImpl.userCanDownloadAttachment(attachment);

        assertTrue(result);
    }

    @Test
    void groupManagerCanDownloadAttachmentInSameDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = attachmentAuthorizationImpl.userCanDownloadAttachment(attachment);

        assertTrue(result);
    }

    @Test
    void groupManagerCannotDownloadAttachmentInDifferentDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        // Görevin departmanını değiştir
        Project differentProject = new Project();
        differentProject.setId(2L);
        differentProject.setDepartment(department2);
        task.setProject(differentProject);

        boolean result = attachmentAuthorizationImpl.userCanDownloadAttachment(attachment);

        assertFalse(result);
    }

    @Test
    void teamMemberCanDownloadAttachmentFromAssignedTask() {
        when(authentication.getPrincipal()).thenReturn(teamMember);

        boolean result = attachmentAuthorizationImpl.userCanDownloadAttachment(attachment);

        assertTrue(result);
    }


    @Test
    void teamLeaderCanDeleteAttachment() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = attachmentAuthorizationImpl.userCanDeleteAttachment(attachment);

        assertTrue(result);
    }

    @Test
    void groupManagerCanDeleteAttachmentInSameDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = attachmentAuthorizationImpl.userCanDeleteAttachment(attachment);

        assertTrue(result);
    }

    @Test
    void teamMemberCanDeleteOwnAttachmentFromAssignedTask() {
        when(authentication.getPrincipal()).thenReturn(teamMember);

        boolean result = attachmentAuthorizationImpl.userCanDeleteAttachment(attachment);

        assertTrue(result);
    }

    @Test
    void teamMemberCannotDeleteAttachmentFromUnassignedTask() {
        when(authentication.getPrincipal()).thenReturn(otherTeamMember);

        Exception exception = assertThrows(UnauthorizedAccessException.class, () -> {
            attachmentAuthorizationImpl.userCanDeleteAttachment(attachment);
        });

        assertTrue(exception.getMessage().contains("USER_CANNOT_DELETE_FILES_FROM_UNASSIGNED_TASKS"));
    }



    @Test
    void teamLeaderCanViewTaskAttachments() {
        when(authentication.getPrincipal()).thenReturn(teamLeader);

        boolean result = attachmentAuthorizationImpl.userCanViewTaskAttachments(task);

        assertTrue(result);
    }

    @Test
    void groupManagerCanViewTaskAttachmentsInSameDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        boolean result = attachmentAuthorizationImpl.userCanViewTaskAttachments(task);

        assertTrue(result);
    }

    @Test
    void groupManagerCannotViewTaskAttachmentsInDifferentDepartment() {
        when(authentication.getPrincipal()).thenReturn(groupManager);

        // Görevin departmanını değiştir
        Project differentProject = new Project();
        differentProject.setId(2L);
        differentProject.setDepartment(department2);
        task.setProject(differentProject);

        boolean result = attachmentAuthorizationImpl.userCanViewTaskAttachments(task);

        assertFalse(result);
    }

    @Test
    void teamMemberCanViewAttachmentsFromAssignedTask() {
        when(authentication.getPrincipal()).thenReturn(teamMember);

        boolean result = attachmentAuthorizationImpl.userCanViewTaskAttachments(task);

        assertTrue(result);
    }

    @Test
    void teamMemberCannotViewAttachmentsFromUnassignedTask() {
        when(authentication.getPrincipal()).thenReturn(otherTeamMember);

        Exception exception = assertThrows(UnauthorizedAccessException.class, () -> {
            attachmentAuthorizationImpl.userCanViewTaskAttachments(task);
        });

        assertTrue(exception.getMessage().contains("USER_CANNOT_VIEW_ATTACHMENTS_FROM_UNASSIGNED_TASKS"));
    }

}
