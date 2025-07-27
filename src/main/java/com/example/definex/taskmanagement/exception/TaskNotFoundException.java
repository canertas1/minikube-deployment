package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends BaseException{
    public TaskNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
