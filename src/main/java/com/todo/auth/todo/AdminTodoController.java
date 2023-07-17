package com.todo.auth.todo;

import com.todo.auth.user.User;
import com.todo.auth.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminTodoController {
    private final TodoService todoService;
    private final UserRepository userRepository;

    @Autowired
    public AdminTodoController(TodoService todoService, UserRepository userRepository) {
        this.todoService = todoService;
        this.userRepository = userRepository;
    }

    @GetMapping("/allTodos")
    public Page<Todo> allTodos(Pageable pageable){
        return todoService.allTodos(pageable);
    }

    @GetMapping("/{id}")
    public Todo findTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id);
    }

    @GetMapping("/users")
    public List<User> getUsers(){
        return userRepository.findAll();
    }

    @GetMapping("/listTodoByUsername/{userId}")
    public List<Todo> findTodosByUser(@PathVariable Long userId) {
        return todoService.getTodosForUser(userId);
    }
}
