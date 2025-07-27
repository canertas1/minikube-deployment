package com.example.definex.taskmanagement.authorization;

import com.example.definex.taskmanagement.entities.Task;

public interface CommentAuthorization {
    void userCanReachComment(Task task);

}
