package com.todo.auth.todo;

import com.todo.auth.user.User;
import com.todo.auth.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminTodoController {
    private TodoService todoService;
    private UserRepository userRepository;

    @Autowired
    public AdminTodoController(TodoService todoService, UserRepository userRepository) {
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    @GetMapping("/allTodos")
    public List<Todo> allTodos(){
        return todoService.allTodos();
    }

    @GetMapping("/{id}")
    public Todo findTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id);
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return userRepository.findAll();
    }
}
