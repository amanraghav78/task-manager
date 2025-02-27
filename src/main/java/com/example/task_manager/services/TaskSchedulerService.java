package com.example.task_manager.services;

import com.example.task_manager.entities.Task;
import com.example.task_manager.repositories.TaskRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskSchedulerService {
    private final TaskRepository taskRepository;
    private final NotificationService notificationService;


    public TaskSchedulerService(TaskRepository taskRepository, NotificationService notificationService) {
        this.taskRepository = taskRepository;
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void checkTaskDeadlines(){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime upcomingDeadline = now.plusHours(24);

        List<Task> dueTasks = taskRepository.findByDueDateBetween(now, upcomingDeadline);
        dueTasks.forEach(notificationService::sendReminder);
    }
}
