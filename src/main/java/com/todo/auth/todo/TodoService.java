package com.todo.auth.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    @Autowired
    private TodoRepository todoRepository;


    public Todo saveTodo(Todo todo) {
        System.out.println(todo.toString());
        return todoRepository.save(todo);
    }

    //Optional!
    public List<Todo> saveTodos(List<Todo> todoList) {
        return todoRepository.saveAll(todoList);
    }

    //GET
    public List<Todo> getTodos() {
        return todoRepository.findAll();
    }
    public Todo getTodoById(Long id) {
        return todoRepository.findById(id).orElse(null);
    }
    public Todo getTodoByName(String header) {
        return todoRepository.findByHeader(header);
    }
    public List<Todo> getTodosForUser(String username) {
        return todoRepository.findAllByUsername(username);
    }

    //PUT
    public Todo updateTodo(Todo todo) {
        System.out.println("updates");
        Todo existing_todo = todoRepository.findById(todo.getId()).orElse(null);
        existing_todo.setHeader(todo.getHeader());
        existing_todo.setDescription(todo.getDescription());
        existing_todo.setTodoStatus(todo.getTodoStatus());
        return todoRepository.save(existing_todo);
    }

    //DELETE
    public String deleteTodo(Long id) {
        todoRepository.deleteById(id);
        return id + " id -> course removed";
    }

}
