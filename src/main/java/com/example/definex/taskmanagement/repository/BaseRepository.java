package com.example.definex.taskmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    @Query("select e from #{#entityName} e where e.isDeleted = false")
    List<T> findAll();
    @Query("select e from #{#entityName} e where e.isDeleted = false and e.id = ?1")
    Optional<T> findById(ID id);
    @Query("select count(e) > 0 from #{#entityName} e where e.isDeleted = false and e.id = ?1")
    boolean existsById(ID id);
}
