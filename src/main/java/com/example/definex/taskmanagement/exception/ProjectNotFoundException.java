package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class ProjectNotFoundException extends BaseException{
    public ProjectNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
