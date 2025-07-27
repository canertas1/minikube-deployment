package com.example.definex.taskmanagement.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Attachment extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String filePath;
    private String fileName;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-attachments")
    private User user;
    @ManyToOne
    @JoinColumn(name = "task_id")
    @JsonBackReference(value = "task-attachments")
    private Task task;
}
