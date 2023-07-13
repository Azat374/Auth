package com.todo.auth.todo;

import com.todo.auth.email.EmailService;
import com.todo.auth.exception.NotFoundException;
import com.todo.auth.user.User;
import com.todo.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
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
@Slf4j
public class TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final EmailService emailService;

    private User getUserFromToken(){
        log.debug("Trying to get user from jwt token");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof User user) {
            return user;
        }
        throw new NotFoundException("User not found");
    }

    public Todo saveTodo(TodoResponse todoResponse) {
        log.debug("Trying to save todo {}", todoResponse);
        User user = getUserFromToken();
        Todo todo = toTodo(todoResponse, user);
        return todoRepository.save(todo);
    }

    public List<Todo> saveTodos(List<TodoResponse> todoList) {
        log.debug("Trying to save todos {}", todoList);
        User user = getUserFromToken();
        List<Todo> todos = toTodos(todoList, user);
        System.out.println(todos.toString());
        return todoRepository.saveAll(todos);
    }

    //GET
    public List<Todo> allTodos(){
        log.debug("Trying to get all todos (for admin)");
        return todoRepository.findAll();
    }

    public List<TodoResponse> getTodos(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String keyword){
        log.debug("Trying to get todos with/without filter: status - {}, keyword - {}", status, keyword);
        User user = getUserFromToken();
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
        log.debug("Trying to get todo by id");
        return todoRepository.findById(id).orElse(null);
    }
    public Todo getTodoByName(String header) {
        log.debug("Trying to get todo by header");
        return todoRepository.findByHeader(header);
    }
    public List<Todo> getTodosForUser(Long userId) {
        log.debug("Trying to get todos by user (for admin)");
        return todoRepository.findAllByUserId(userId);
    }

    //PUT
    public Todo updateTodo(Todo todo) {
        log.debug("Trying to update todo");
        System.out.println("updates");
        Todo existing_todo = todoRepository.findById(todo.getId()).orElse(null);
        existing_todo.setHeader(todo.getHeader());
        existing_todo.setDescription(todo.getDescription());
        existing_todo.setTodoStatus(todo.getTodoStatus());
        return todoRepository.save(existing_todo);
    }

    //DELETE
    public String deleteTodo(Long id) {
        log.debug("Trying to delete todo");
        todoRepository.deleteById(id);
        return id + " id -> todo removed";
    }

    public List<TodoResponse> getTodayTodos() {
        log.debug("Trying to get today todos");
        User user = getUserFromToken();
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByUserAndTargetDate(user, today);

        return mapToTodoResponses(todos);
    }


    private List<TodoResponse> mapToTodoResponses(List<Todo> todos) {
        log.debug("Trying to convert Todo to TodoResponse");
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
        log.debug("Trying to get overdue todos");
        User user = getUserFromToken();
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByUserAndTargetDateBeforeAndTodoStatusNot(user, today, TodoStatus.FINISH);
        return mapToTodoResponses(todos);
    }

    public ResponseEntity<String> sendDailySummary(User user) {
        log.debug("Trying to send mail about report completed today's tasks");
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByUserAndTodoStatusAndTargetDate(user, TodoStatus.FINISH, today);
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
        String recipientEmail = user.getEmail(); // Замените на фактический адрес получателя

        emailService.sendMail(recipientEmail, subject, message);
        return ResponseEntity.ok("Ежедневная отчет о сделанных задач успешно отправлена");
    }
    @Scheduled(cron = "0 0 22 * * ?")
    private void sendAllSummary(){
        log.debug("Trying to send report to all users");
        List<User> users = userRepository.findAll();
        for(User user : users){
            if (user.getEmail().equals("balgaliazik@gmail.com")){// это чтобы только себе отправить(нужно удалить)
                System.out.println(sendDailySummary(user));
                System.out.println("Отчет для " + user.getFirstname() + " " + user.getLastname() + " отправлен");
            }
        }

    }

    public ResponseEntity<String> statusChange(Long id, String status) {
        log.debug("Trying to change status todo: id - {}, changedStatus - {}", id, status);
        Optional<Todo> todoOptional = todoRepository.findById(id);
        if(todoOptional.isEmpty()){
            throw new NotFoundException("Todo not found");
        }
        Todo todo = todoOptional.get();
        TodoStatus todoStatus = Enum.valueOf(TodoStatus.class, status);
        todo.setTodoStatus(todoStatus);
        todoRepository.save(todo);
        return ResponseEntity.ok("Статус успешно обновлен на " + status);
    }

    private Todo toTodo(TodoResponse todoResponse, User user) {
        log.debug("Trying to convert TodoResponse to  Todo");
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
        log.debug("Trying to convert list TodoResponse to list Todo");
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
