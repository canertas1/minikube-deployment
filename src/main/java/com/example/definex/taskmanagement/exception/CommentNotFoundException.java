package com.example.definex.taskmanagement.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BaseException{
    public CommentNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}