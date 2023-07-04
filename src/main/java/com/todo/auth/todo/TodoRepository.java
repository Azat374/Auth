package com.todo.auth.todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUsername(String username);
    Todo findByHeader(String header);
    List<Todo> findAll();

    List<Todo> findByTargetDate(Date date);

    List<Todo> findByTargetDateBeforeAndTodoStatusNot(Date today, TodoStatus todoStatus);

    List<Todo> findByTodoStatusAndTargetDate(TodoStatus todoStatus, Date today);

    List<Todo> findByTodoStatusAndHeaderContainingIgnoreCase(TodoStatus todoStatus, String keyword);

    List<Todo> findByTodoStatus(TodoStatus todoStatus);

    List<Todo> findByHeaderContainingIgnoreCase(String keyword);
}

