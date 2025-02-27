package com.example.task_manager.services;

import com.example.task_manager.entities.Task;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    private final JavaMailSender mailSender;


    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendReminder(Task task){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setTo("amanraghav682@gmail.com");
        message.setSubject("Task reminder " + task.getTitle());
        message.setText("Your task " + task.getTitle() + " is due on " + task.getDueDate());

        mailSender.send(message);
    }
}
