package com.todo.auth.todo;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.todo.auth.email.EmailService;
import com.todo.auth.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableRabbit
public class TodoReportService {
    private final TodoRepository todoRepository;
    private final EmailService emailService;


    @RabbitListener(queues = "queue1")
    public void sendDailySummary(User user) {
        log.debug("Trying to send mail about report completed today's tasks");
        String pdfFilePath = "src/main/resources/pdf/report.pdf";
        String FONT = "src/main/resources/Arial.ttf";
        try{
            Document document = new Document();
            BaseFont bf=BaseFont.createFont(FONT, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font=new Font(bf,30,Font.NORMAL);
            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
            document.open();
            LocalDate today = LocalDate.now();
            List<Todo> todosFinish = todoRepository.findByUserAndTodoStatusAndTargetDate(user, TodoStatus.FINISH, today);
            List<Todo> todosNotFinish = todoRepository.findByUserAndTodoStatusIsNotAndTargetDate(user, TodoStatus.FINISH, today);

            StringBuilder messageBuilder = new StringBuilder();
            //messageBuilder.append("Список задач пользователя " + user.getFirstname() + " " + user.getLastname() + " на сегодня:\n");
            document.add(new Paragraph("Список задач пользователя " + user.getFirstname() + " " + user.getLastname() + " на сегодня:",font));
            //messageBuilder.append("Что сделано:\n");
            document.add(new Paragraph("Что сделано:",font));
            if (todosFinish.isEmpty()) {
                //messageBuilder.append("Отсутствует");
                document.add(new Paragraph("Отсутствует", font));
            } else {
                for (Todo todo : todosFinish) {
                    String t = "- " + todo.getHeader() + " - " + todo.getDescription() + " - " + todo.getTodoStatus() + "\n";
                    //messageBuilder.append(t);
                    document.add(new Paragraph(t, font));
                }
            }

            //messageBuilder.append("Что не сделано:\n");
            document.add(new Paragraph("Что не сделано:", font));
            if (todosNotFinish.isEmpty()) {
                //messageBuilder.append("Отсутствует");
                document.add(new Paragraph("Отсутствует", font));
            } else {
                for (Todo todo : todosNotFinish) {
                    String t = "- " + todo.getHeader() + " - " + todo.getDescription() + " - " + todo.getTodoStatus() + "\n";
                    //messageBuilder.append(t);
                    document.add(new Paragraph(t, font));
                }
            }
            document.close();
        } catch (IOException | DocumentException e) {
            // Обработка исключений при создании PDF-файла
            e.printStackTrace();
        }

        String subject = "Todo list";
        String message = "Ежедневное резюме выполненных задач";
        String recipientEmail = user.getEmail(); // Замените на фактический адрес получателя
        emailService.sendMessageWithPdf(recipientEmail, subject, message, "pdf/report.pdf");
    }

}
