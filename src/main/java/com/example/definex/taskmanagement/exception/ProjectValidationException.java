package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class ProjectValidationException extends BaseException{
    public ProjectValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
