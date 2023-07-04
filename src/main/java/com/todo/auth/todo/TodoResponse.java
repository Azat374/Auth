package com.todo.auth.todo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoResponse {
    private Long id;
    private String header;
    private String description;
    private LocalDate targetDate;
    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;
}
