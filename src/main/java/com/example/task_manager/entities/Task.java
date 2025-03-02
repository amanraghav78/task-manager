package com.example.task_manager.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.concurrent.RecursiveTask;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status= TaskStatus.TODO;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime dueDate;

    private Integer orderIndex;

    @Column(name = "file_url")
    private String fileUrl;

    public String getFileUrl(){
        return fileUrl;
    }

    public void setFileUrl(String fileUrl){
        this.fileUrl = fileUrl;
    }

    public Integer getOrderIndex(){
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex){
        this.orderIndex= orderIndex;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate= dueDate;
    }



    @PrePersist
    protected void onCreate(){
        this.createdAt= LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt= LocalDateTime.now();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }



}
