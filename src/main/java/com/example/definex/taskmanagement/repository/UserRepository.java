package com.example.definex.taskmanagement.repository;

import com.example.definex.taskmanagement.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User,Long> {
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM User e WHERE e.isDeleted = false AND e.email = :email")
    boolean existsByEmail(String email);

    @Query("SELECT e FROM User e WHERE e.isDeleted = false AND e.email = :email")
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.isDeleted = false")
    Page<User> findAll(Pageable pageable);
}
