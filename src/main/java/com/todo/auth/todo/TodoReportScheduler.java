package com.todo.auth.todo;

import com.todo.auth.user.User;
import com.todo.auth.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoReportScheduler {
    private final UserRepository userRepository;
    private final TodoReportService reportService;
    private final RabbitTemplate rabbitTemplate;
    @Scheduled(cron = "0 5 17 * * ?")
    private void sendAllSummary() {
        log.debug("Trying to send report to all users");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals("balgaliazik@gmail.com")) {// это чтобы только себе отправить(нужно удалить)
                rabbitTemplate.convertAndSend("testExchange", "testRoutingKey", user);
                log.debug("Report for {} {} finished with status true", user.getFirstname(), user.getLastname());
            }
        }

    }
}
