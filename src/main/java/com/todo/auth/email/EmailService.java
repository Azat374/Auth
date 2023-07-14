package com.todo.auth.email;


public interface EmailService  {
    String sendMail(String to, String subject, String body);
}
