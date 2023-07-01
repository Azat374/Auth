package com.todo.auth.todo;

import com.todo.auth.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(name = "/api/v1/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;
    private User user;
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @GetMapping
    public ResponseEntity<String> Hello(){
        System.out.println("TODO");
        return ResponseEntity.ok("Hello from secured");
    }
    //POST
    @PostMapping("/addTodo")
    public Todo addTodo(@RequestBody Todo Todo) {
        logger.info("Todo object {}", Todo.toString());
        return todoService.saveTodo(Todo);
    }

    @PostMapping("/addTodos")
    public List<Todo> addTodos(@RequestBody List<Todo> Todos) {
        return todoService.saveTodos(Todos);
    }

    //GET
    @GetMapping("/Todos")
    public List<Todo> getAllTodos() {
        return todoService.getTodos();
    }
    @GetMapping("/TodoById/{id}")
    public Todo findTodoById(@PathVariable Long id) {
        return todoService.getTodoById(id);
    }
    @GetMapping("/TodoByName/{name}")
    public Todo findTodoByName(@PathVariable String name) {
        return todoService.getTodoByName(name);
    }
    @GetMapping("/listTodoByUsername/{username}")
    public List<Todo> findTodosByUsername(@PathVariable String username) {
        return todoService.getTodosForUser(username);
    }

    //PUT
    @PutMapping("/update")
    public Todo updateTodo(@RequestBody Todo Todo)
    {
        System.out.println("UPDATED");
        return todoService.updateTodo(Todo);
    }


    //DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteTodo(@PathVariable Long id) {
        return todoService.deleteTodo(id);
    }

}
