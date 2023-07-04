package com.todo.auth.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<TodoResponse> getTodos(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String keyword){
        List<Todo> todos;
        if (status != null && keyword != null) {
            todos = todoRepository.findByTodoStatusAndHeaderContainingIgnoreCase(TodoStatus.valueOf(status), keyword);
        } else if (status != null) {
            todos = todoRepository.findByTodoStatus(TodoStatus.valueOf(status));
        } else if (keyword != null) {
            todos = todoRepository.findByHeaderContainingIgnoreCase(keyword);
        } else {
            todos = todoRepository.findAll();
        }

        return mapToTodoResponses(todos);
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

    public List<TodoResponse> getTodayTodos() {
        Date today = new Date();
        System.out.println(today);
        List<Todo> todos = todoRepository.findByTargetDate(today);

        return mapToTodoResponses(todos);
    }


    private List<TodoResponse> mapToTodoResponses(List<Todo> todos) {
        return todos.stream()
                .map(todo -> new TodoResponse(
                        todo.getId(),
                        todo.getHeader(),
                        todo.getDescription(),
                        todo.getTargetDate(),
                        todo.getTodoStatus(),
                        todo.getUsername()
                ))
                .collect(Collectors.toList());
    }


    public List<TodoResponse> getOverdueTodos() {
        Date today = new Date();
        List<Todo> todos = todoRepository.findByTargetDateBeforeAndTodoStatusNot(today, TodoStatus.FINISH);
        return mapToTodoResponses(todos);
    }

//    public void sendDailySummary() {
//        LocalDate today = LocalDate.now();
//        List<Todo> todos = todoRepository.findByTodoStatusAndTargetDate(TodoStatus.FINISH, today);
//        if (todos.isEmpty()) {
//            return;
//        }
//
//        StringBuilder messageBuilder = new StringBuilder();
//        messageBuilder.append("Список выполненных задач на сегодня:\n\n");
//        for (Todo todo : todos) {
//            messageBuilder.append("- ").append(todo.getDescription()).append("\n");
//        }
//
//        String subject = "Ежедневное резюме выполненных задач";
//        String message = messageBuilder.toString();
//
//        String recipientEmail = "balgaliazik@gmail.com"; // Замените на фактический адрес получателя
//
//        emailService.sendEmail(recipientEmail, subject, message);
//    }
}
