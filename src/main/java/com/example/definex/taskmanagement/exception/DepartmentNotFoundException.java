package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class DepartmentNotFoundException extends BaseException{
    public DepartmentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
