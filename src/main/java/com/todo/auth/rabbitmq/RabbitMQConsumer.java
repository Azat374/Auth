package com.todo.auth.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
@Slf4j
public class RabbitMQConsumer {


    @RabbitListener(queues = "queue1")
    public void processMyQueue(String message) {
        log.info("Received from myQueue : {} ", new String(message.getBytes()));
    }
}