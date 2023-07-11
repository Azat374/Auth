package com.todo.auth.exception;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private int errorCode;
    private LocalDateTime timestamp;
    private String message;
    public ErrorResponse(int errorCode, String message) {
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }
}
