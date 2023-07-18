package com.todo.auth.rabbitmq;

public interface RabbitMQProducerService {
    void sendMessage(String message, String routingKey);
}
