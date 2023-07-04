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
@RequestMapping("/api/v1/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;
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
//    @GetMapping("/Todos")
//    public List<Todo> getAllTodos() {
//        return todoService.getTodos();
//    }
    @GetMapping("/Todos")
    public ResponseEntity<List<TodoResponse>> getTodos(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String keyword){
        List<TodoResponse> todos = todoService.getTodos(status, keyword);
        return ResponseEntity.ok(todos);
    }
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
    //Сегоднешние задачи
    @GetMapping("/Todos/today")
    public ResponseEntity<List<TodoResponse>> getTodayTodos() {
        List<TodoResponse> todos = todoService.getTodayTodos();
        return ResponseEntity.ok(todos);
    }
    //Нереализованные задачи с датой реализации прошедшего дня
    @GetMapping("/Todos/overdue")
    public ResponseEntity<List<TodoResponse>> getOverdueTodos() {
        List<TodoResponse> todos = todoService.getOverdueTodos();
        return ResponseEntity.ok(todos);
    }

//    @PostMapping("/Todos/daily-summary")
//    public ResponseEntity<String> sendDailySummary() {
//        todoService.sendDailySummary();
//        return ResponseEntity.ok("Daily summary sent successfully");
//    }

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
