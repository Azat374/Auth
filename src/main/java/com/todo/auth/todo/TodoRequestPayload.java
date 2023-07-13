package com.todo.auth.todo;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequestPayload {
    private Long id;
    @NotBlank
    private String header;
    @NotBlank
    private String description;
    @NotNull
    private LocalDate targetDate;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;
}
