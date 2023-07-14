package com.todo.auth.todo;

import com.todo.auth.user.User;
import com.todo.auth.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TodoReportScheduler {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TodoReportService reportService;

    @Scheduled(cron = "0 0 22 * * ?")
    private void sendAllSummary() {
        log.debug("Trying to send report to all users");
        List<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getEmail().equals("balgaliazik@gmail.com")) {// это чтобы только себе отправить(нужно удалить)
                boolean res = reportService.sendDailySummary(user);
                log.debug("Report for {} {} finished with status {}", user.getFirstname(), user.getLastname(), res);
            }
        }

    }
}
