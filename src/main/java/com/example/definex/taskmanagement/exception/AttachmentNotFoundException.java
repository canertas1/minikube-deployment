package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class AttachmentNotFoundException extends BaseException{
    public AttachmentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
