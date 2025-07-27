package com.example.definex.taskmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String userStoryDescription;
    private String acceptanceCriteria;
    @Enumerated(EnumType.STRING)
    private TaskStateType state;
    @Enumerated(EnumType.STRING)
    private TaskPriorityType priority;
    private String stateChangeReason;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonBackReference(value = "project-tasks")
    private Project project;
    @ManyToOne
    @JoinColumn(name = "assignee_id")
    @JsonBackReference(value = "user-tasks")
    private User assignee;
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "task-comments")
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "task-attachments")
    private List<Attachment> attachments = new ArrayList<>();
}