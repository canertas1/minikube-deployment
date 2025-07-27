package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class DepartmentValidationException extends BaseException{
    public DepartmentValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
