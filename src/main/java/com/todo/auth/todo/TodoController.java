package com.todo.auth.todo;

import com.todo.auth.user.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
public class TodoController {
    @Autowired
    private TodoService todoService;
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private User getUserFromToken(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)auth.getPrincipal();
        return user;
    }

    //POST
    @PostMapping("/add-todo")
    public Todo addTodo(@RequestBody TodoResponse Todo) {

        logger.info("Todo object {}", Todo.toString());
        return todoService.saveTodo(getUserFromToken(), Todo);
    }

    @PostMapping("/add-todos")
    public List<Todo> addTodos(@RequestBody List<TodoResponse> Todos) {

        return todoService.saveTodos(Todos, getUserFromToken());
    }


    //GET
    @GetMapping()
    public ResponseEntity<List<TodoResponse>> getTodos(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String keyword){
        List<TodoResponse> todos = todoService.getTodos(status, keyword,getUserFromToken());
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/TodoByName/{name}")
    public Todo findTodoByName(@PathVariable String name) {
        return todoService.getTodoByName(name);
    }
    @GetMapping("/listTodoByUsername/{username}")
    public List<Todo> findTodosByUser(@PathVariable Long userId) {
        return todoService.getTodosForUser(userId);
    }
    //Сегоднешние задачи
    @GetMapping("/today")
    public ResponseEntity<List<TodoResponse>> getTodayTodos() {
        List<TodoResponse> todos = todoService.getTodayTodos();
        return ResponseEntity.ok(todos);
    }
    //Нереализованные задачи с датой реализации прошедшего дня
    @GetMapping("/overdue")
    public ResponseEntity<List<TodoResponse>> getOverdueTodos() {
        List<TodoResponse> todos = todoService.getOverdueTodos();
        return ResponseEntity.ok(todos);
    }

    @PostMapping("/status-change")
    public ResponseEntity<String> statusChange(@RequestParam Long id, @RequestParam String status) {
        return todoService.statusChange(id, status);
    }

    @PostMapping("/daily-summary")
    public ResponseEntity<String> sendDailySummary() {
        return todoService.sendDailySummary();
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
