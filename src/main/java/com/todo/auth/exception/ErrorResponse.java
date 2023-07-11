package com.todo.auth.exception;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private Long id;
    private int errorCode;
    private LocalDateTime timestamp;
    private String errorMessage;
}
