package com.todo.auth.email;


import com.itextpdf.text.pdf.PdfDocument;

public interface EmailService  {
    String sendMail(String to, String subject, String body);
    String sendMessageWithPdf(String to, String subject, String body, String pathToAttachment);
}
