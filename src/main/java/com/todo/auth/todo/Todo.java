package com.todo.auth.todo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Temporal(TemporalType.TIMESTAMP)
    private Date targetDate;
    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;
    @Column(name = "username")
    private String username;


}
