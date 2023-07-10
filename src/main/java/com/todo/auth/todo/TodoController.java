package com.todo.auth.todo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
public class TodoController {
    private final TodoService todoService;
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);
    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    //POST
    @PostMapping("/add-todo")
    public Todo addTodo(@RequestBody TodoResponse Todo) {

        logger.info("Todo object {}", Todo.toString());
        return todoService.saveTodo(Todo);
    }

    @PostMapping("/add-todos")
    public List<Todo> addTodos(@RequestBody List<TodoResponse> Todos) {

        return todoService.saveTodos(Todos);
    }


    //GET
    @GetMapping()
    public ResponseEntity<List<TodoResponse>> getTodos(@RequestParam(required = false) String status,
                                                       @RequestParam(required = false) String keyword){
        List<TodoResponse> todos = todoService.getTodos(status, keyword);
        return ResponseEntity.ok(todos);
    }

    @GetMapping("/TodoByName/{name}")
    public Todo findTodoByName(@PathVariable String name) {
        return todoService.getTodoByName(name);
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

    @PostMapping("/status-change/{id}")
    public ResponseEntity<String> statusChange(@PathVariable Long id, @RequestParam String status) {
        return todoService.statusChange(id, status);
    }

    @PostMapping("/k")
    public ResponseEntity<String> k(){
        return ResponseEntity.ok("k");
    }
//    @PostMapping("/daily-summary")
//    public ResponseEntity<String> sendDailySummary() {
//        return todoService.sendDailySummary(getUserFromToken());
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
