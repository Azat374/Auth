package com.todo.auth.todo;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.todo.auth.email.EmailService;
import com.todo.auth.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final EmailService emailService;

    public Todo saveTodo(User user, TodoResponse todoResponse) {
        Todo todo = toTodo(todoResponse, user);
        System.out.println(todo.toString());
        return todoRepository.save(todo);
    }

    //Optional!
    public List<Todo> saveTodos(List<TodoResponse> todoList, User user) {
        List<Todo> todos = toTodos(todoList, user);
        System.out.println(todos.toString());
        return todoRepository.saveAll(todos);
    }

    //GET
    public List<Todo> allTodos(){
        return todoRepository.findAll();
    }

    public List<TodoResponse> getTodos(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String keyword, User user){

//        List<Todo> todos = todoRepository.findAllByUser(user.getId());
        List<Todo> todos;
        if (status != null && keyword != null) {
            todos = todoRepository.findByUserAndTodoStatusAndHeaderContainingIgnoreCase(user, TodoStatus.valueOf(status), keyword);
        } else if (status != null) {
            todos = todoRepository.findByUserAndTodoStatus(user, TodoStatus.valueOf(status));
        } else if (keyword != null) {
            todos = todoRepository.findByUserAndHeaderContainingIgnoreCase(user, keyword);
        } else {
            todos = todoRepository.findAllByUser(user);
        }

        return mapToTodoResponses(todos);
    }
    public Todo getTodoById(Long id) {
        return todoRepository.findById(id).orElse(null);
    }
    public Todo getTodoByName(String header) {
        return todoRepository.findByHeader(header);
    }
    public List<Todo> getTodosForUser(Long userId) {
        return todoRepository.findAllByUserId(userId);
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
        LocalDate today = LocalDate.now();
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
                        todo.getTodoStatus()
                ))
                .collect(Collectors.toList());
    }


    public List<TodoResponse> getOverdueTodos() {
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByTargetDateBeforeAndTodoStatusNot(today, TodoStatus.FINISH);
        return mapToTodoResponses(todos);
    }

    public ResponseEntity<String> sendDailySummary() {
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByTodoStatusAndTargetDate(TodoStatus.FINISH, today);
        if (todos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Список выполненных задач пуст");
        }

        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Список выполненных задач на сегодня:\n\n");
        for (Todo todo : todos) {
            messageBuilder.append("- ").append(todo.getDescription()).append("\n");
        }

        String subject = "Ежедневное резюме выполненных задач";
        String message = messageBuilder.toString();

        String recipientEmail = "balgaliazik@gmail.com"; // Замените на фактический адрес получателя

        emailService.sendMail(recipientEmail, subject, message);
        return ResponseEntity.ok("Ежедневная отчет о сделанных задач успешно отправлена");
    }

    public ResponseEntity<String> statusChange(Long id, String status) {
        Optional<Todo> todoOptional = todoRepository.findById(id);
        if(todoOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Задача не найдена");
        }
        Todo todo = todoOptional.get();
        TodoStatus todoStatus = Enum.valueOf(TodoStatus.class, status);
        todo.setTodoStatus(todoStatus);
        todoRepository.save(todo);
        return ResponseEntity.ok("Статус успешно обновлен на " + status);
    }

    private Todo toTodo(TodoResponse todoResponse, User user) {
        return Todo.builder()
                .id(todoResponse.getId())
                .header(todoResponse.getHeader())
                .description(todoResponse.getDescription())
                .targetDate(todoResponse.getTargetDate())
                .todoStatus(todoResponse.getTodoStatus())
                .user(user)
                .build();

    }

    private List<Todo> toTodos(List<TodoResponse> todoResponses, User user) {
        return todoResponses.stream()
                .map(todo -> new Todo(
                        todo.getId(),
                        todo.getHeader(),
                        todo.getDescription(),
                        todo.getTargetDate(),
                        todo.getTodoStatus(),
                        user
                ))
                .collect(Collectors.toList());
    }
}
