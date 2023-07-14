package com.todo.auth.todo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
public class TodoController {
    private final com.todo.auth.todo.TodoService todoService;
    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    //POST
    @PostMapping
    public com.todo.auth.todo.Todo addTodo(@RequestBody @Valid TodoRequestPayload Todo) {
        return todoService.saveTodo(Todo);
    }

    @PostMapping("/batch")
    public List<com.todo.auth.todo.Todo> addTodos(@RequestBody @Valid @NotEmpty List<@Valid TodoRequestPayload> Todos) {

        return todoService.saveTodos(Todos);
    }


    //GET
    @GetMapping()
    public Page<TodoRequestPayload> getTodos(Pageable pageable, @RequestParam(required = false) String status,
                                                             @RequestParam(required = false) String keyword){
        return todoService.getTodos(status, keyword, pageable);
    }

    @GetMapping("/by-name/{name}")
    public com.todo.auth.todo.Todo findTodoByName(@PathVariable String name) {
        return todoService.getTodoByName(name);
    }

    //Сегоднешние задачи
    @GetMapping("/today")
    public ResponseEntity<List<TodoRequestPayload>> getTodayTodos() {
        List<TodoRequestPayload> todos = todoService.getTodayTodos();
        return ResponseEntity.ok(todos);
    }
    //Нереализованные задачи с датой реализации прошедшего дня
    @GetMapping("/overdue")
    public ResponseEntity<List<TodoRequestPayload>> getOverdueTodos() {
        List<TodoRequestPayload> todos = todoService.getOverdueTodos();
        return ResponseEntity.ok(todos);
    }

    @PostMapping("/status-change/{id}")
    public ResponseEntity<String> statusChange(@PathVariable Long id, @RequestParam TodoStatus status) {
        return todoService.statusChange(id, status);
    }

    //PUT
    @PutMapping("/update")
    public com.todo.auth.todo.Todo updateTodo(@RequestBody Todo Todo)
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
