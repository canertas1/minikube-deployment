package com.example.definex.taskmanagement.config;

import com.example.definex.taskmanagement.entities.AbstractBaseEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDateTime;

public class AuditEntityListener {
    @PrePersist
    public void prePersist(AbstractBaseEntity entity) {
        entity.setCreatedAt(LocalDateTime.now());
        entity.setCreatedBy(getCurrentUsername());
    }
    @PreUpdate
    public void preUpdate(AbstractBaseEntity entity) {
        entity.setUpdatedAt(LocalDateTime.now());
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "system";
    }
}
