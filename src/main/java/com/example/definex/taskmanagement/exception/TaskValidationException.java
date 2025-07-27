package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class TaskValidationException extends BaseException{
    public TaskValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
