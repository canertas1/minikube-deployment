package com.example.definex.taskmanagement.repository;

import com.example.definex.taskmanagement.entities.Attachment;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AttachmentRepository extends BaseRepository<Attachment,Long> {
    @Query("SELECT a FROM Attachment a WHERE a.isDeleted = false AND a.task.id = :id")
    List<Attachment> findByTask_Id(Long id);
}
