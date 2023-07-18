package com.todo.auth.todo;

import com.todo.auth.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUser(User user);
    List<Todo> findAllByUserId(Long userId);
    Todo findByHeader(String header);


    List<Todo> findAll();

    List<Todo> findByUserAndTargetDate(User user, LocalDate date);

    List<Todo> findByUserAndTargetDateBeforeAndTodoStatusNot(User user, LocalDate today, TodoStatus todoStatus);

    List<Todo> findByUserAndTodoStatusAndTargetDate(User user, TodoStatus todoStatus, LocalDate today);
    List<Todo> findByUserAndTodoStatusIsNotAndTargetDate(User user, TodoStatus todoStatus, LocalDate today);

    List<Todo> findByUserAndTodoStatusAndHeaderContainingIgnoreCase(User user, TodoStatus todoStatus, String keyword);

    List<Todo> findByUserAndTodoStatus(User user, TodoStatus todoStatus);

    List<Todo> findByUserAndHeaderContainingIgnoreCase(User user, String keyword);
}

