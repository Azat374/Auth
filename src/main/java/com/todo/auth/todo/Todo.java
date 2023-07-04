package com.todo.auth.todo;

import com.todo.auth.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "todos")
public class Todo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String header;
    private String description;
    @Temporal(TemporalType.DATE)
    private LocalDate targetDate;
    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
