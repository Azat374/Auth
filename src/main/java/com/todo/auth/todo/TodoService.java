package com.todo.auth.todo;

import com.todo.auth.email.EmailService;
import com.todo.auth.exception.NotFoundException;
import com.todo.auth.user.User;
import com.todo.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodoService {
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;
    private final EmailService emailService;

    @Transactional
    public Todo saveTodo(TodoRequestPayload todoResponse) {
        log.debug("Trying to save todo {}", todoResponse);
        User user = getUserFromToken();
        Todo todo = toTodo(todoResponse, user);
        return todoRepository.save(todo);
    }

    @Transactional
    public List<Todo> saveTodos(List<TodoRequestPayload> todoList) {
        log.debug("Trying to save todos {}", todoList);
        User user = getUserFromToken();
        List<Todo> todos = toTodos(todoList, user);
        return todoRepository.saveAll(todos);
    }

    // TODO pageable request - https://www.baeldung.com/rest-api-pagination-in-spring
    @Transactional(readOnly = true)
    public Page<Todo> allTodos(Pageable pageable) {
        log.debug("Trying to get all todos (for admin)");
        Page<Todo> page = todoRepository.findAll(pageable);
        return page;
    }

    // TODO pageable request - https://www.baeldung.com/rest-api-pagination-in-spring
    public List<TodoRequestPayload> getTodos(@RequestParam(required = false) String status,
                                             @RequestParam(required = false) String keyword) {
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
        log.debug("Trying to get todo by id = {}", id);
        return todoRepository.findById(id).orElse(null);
    }

    public Todo getTodoByName(String header) {
        log.debug("Trying to get todo by header");
        return todoRepository.findByHeader(header);
    }

    public List<Todo> getTodosForUser(Long userId) {
        log.debug("Trying to get todos by user with id = {} (for admin)", userId);
        return todoRepository.findAllByUserId(userId);
    }

    //PUT
    public Todo updateTodo(Todo todo) {
        log.debug("Trying to update todo {}", todo);
        Todo existingTodo = todoRepository.findById(todo.getId()).orElse(null);
        existingTodo.setHeader(todo.getHeader());
        existingTodo.setDescription(todo.getDescription());
        existingTodo.setTodoStatus(todo.getTodoStatus());
        return todoRepository.save(existingTodo);
    }

    public String deleteTodo(Long id) {
        log.debug("Trying to delete todo");
        todoRepository.deleteById(id);
        return id + " id -> todo removed";
    }

    public List<TodoRequestPayload> getTodayTodos() {
        log.debug("Trying to get today todos");
        User user = getUserFromToken();
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByUserAndTargetDate(user, today);

        return mapToTodoResponses(todos);
    }


    // TODO MAPSTRUCT
    private List<TodoRequestPayload> mapToTodoResponses(List<Todo> todos) {
        log.debug("Trying to convert Todo to TodoResponse");
        return todos.stream()
                .map(todo -> new TodoRequestPayload(
                        todo.getId(),
                        todo.getHeader(),
                        todo.getDescription(),
                        todo.getTargetDate(),
                        todo.getTodoStatus()
                ))
                .collect(Collectors.toList());
    }


    public List<TodoRequestPayload> getOverdueTodos() {
        log.debug("Trying to get overdue todos");
        User user = getUserFromToken();
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByUserAndTargetDateBeforeAndTodoStatusNot(user, today, TodoStatus.FINISH);
        return mapToTodoResponses(todos);
    }



    public ResponseEntity<String> statusChange(Long id, TodoStatus status) {
        log.debug("Trying to change status todo: id - {}, changedStatus - {}", id, status);
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new NotFoundException("Todo not found"));
        todo.setTodoStatus(status);
        todoRepository.save(todo);
        return ResponseEntity.ok("Статус успешно обновлен на " + status);
    }

    private User getUserFromToken() {
        log.debug("Trying to get user from jwt token");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof User user) {
            return user;
        }
        throw new NotFoundException("User not found");
    }

    // TODO MAPSTRUCT
    private List<Todo> toTodos(List<TodoRequestPayload> todoResponses, User user) {
        log.debug("Trying to convert list TodoResponse to list Todo");
        return todoResponses.stream()
                .map(todo -> toTodo(todo, user))
                .collect(Collectors.toList());
    }

    // TODO MAPSTRUCT
    private Todo toTodo(TodoRequestPayload todoResponse, User user) {
        log.debug("Trying to convert TodoResponse to Todo");
        return Todo.builder()
                .id(todoResponse.getId())
                .header(todoResponse.getHeader())
                .description(todoResponse.getDescription())
                .targetDate(todoResponse.getTargetDate())
                .todoStatus(todoResponse.getTodoStatus())
                .user(user)
                .build();
    }
}
