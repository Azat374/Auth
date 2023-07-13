package com.todo.auth.todo;

import com.todo.auth.email.EmailService;
import com.todo.auth.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TodoReportService {
    private final TodoRepository todoRepository;
    private final EmailService emailService;

    public boolean sendDailySummary(User user) {
        log.debug("Trying to send mail about report completed today's tasks");
        LocalDate today = LocalDate.now();
        List<Todo> todos = todoRepository.findByUserAndTodoStatusAndTargetDate(user, TodoStatus.FINISH, today);
        if (todos.isEmpty()) {
            return true;
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
        return true;
    }

}