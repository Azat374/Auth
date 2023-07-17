package com.todo.auth.todo;

import com.todo.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<com.todo.auth.todo.Todo, Long> {
    List<com.todo.auth.todo.Todo> findAllByUser(User user);
    List<com.todo.auth.todo.Todo> findAllByUserId(Long userId);
    com.todo.auth.todo.Todo findByHeader(String header);


    List<com.todo.auth.todo.Todo> findAll();

    List<com.todo.auth.todo.Todo> findByUserAndTargetDate(User user, LocalDate date);

    List<com.todo.auth.todo.Todo> findByUserAndTargetDateBeforeAndTodoStatusNot(User user, LocalDate today, com.todo.auth.todo.TodoStatus todoStatus);

    List<com.todo.auth.todo.Todo> findByUserAndTodoStatusAndTargetDate(User user, com.todo.auth.todo.TodoStatus todoStatus, LocalDate today);

    List<com.todo.auth.todo.Todo> findByUserAndTodoStatusAndHeaderContainingIgnoreCase(User user, com.todo.auth.todo.TodoStatus todoStatus, String keyword);

    List<com.todo.auth.todo.Todo> findByUserAndTodoStatus(User user, TodoStatus todoStatus);

    List<Todo> findByUserAndHeaderContainingIgnoreCase(User user, String keyword);
}

