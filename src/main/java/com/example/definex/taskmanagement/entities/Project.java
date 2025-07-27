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
@AllArgsConstructor
@NoArgsConstructor
public class Project extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private ProjectStatusType type;

    @JsonBackReference(value = "department-projects")
    @ManyToOne
    @JoinColumn(name = "department_id",referencedColumnName = "id")
    private Department department;

    @JsonManagedReference(value = "project-users")
    @ManyToMany(fetch =FetchType.EAGER, cascade=CascadeType.PERSIST)
    @JoinTable(name = "project_partipicant",
    joinColumns = @JoinColumn(name = "project_id",referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "user_id",referencedColumnName = "id"))
    private List<User> users = new ArrayList<>();
}
