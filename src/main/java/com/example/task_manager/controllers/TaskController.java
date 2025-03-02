package com.example.task_manager.controllers;

import com.example.task_manager.entities.Task;
import com.example.task_manager.entities.TaskPriority;
import com.example.task_manager.entities.TaskStatus;
import com.example.task_manager.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.sqm.mutation.internal.TableKeyExpressionCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")

public class TaskController {

    private TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task){
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id){
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task){
        return ResponseEntity.ok(taskService.updateTask(id, task));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public List<Task> filterTasks(@RequestParam(required = false)TaskStatus status, @RequestParam(required = false)TaskPriority priority){
        if(status != null && priority != null){
            return taskService.getTasksByStatusAndPriority(status, priority);
        } else if(status != null){
            return taskService.getTasksByStatus(status);
        } else if(priority != null){
            return taskService.getTasksByPriority(priority);
        } else {
            return taskService.getAllTasks();
        }
    }

    @PutMapping("/{taskId}/status")
    public ResponseEntity<Task> updateStatus(@PathVariable Long taskId, @RequestParam TaskStatus status) throws Exception {
        Task updatedTask = taskService.updateTaskStatus(taskId, status);
        return ResponseEntity.ok(updatedTask);
    }

    @PostMapping("/{taskId}/attach-file")
    public ResponseEntity<Task> attachFileToTask(@PathVariable Long taskId, @RequestParam("file") String fileUrl){
        return ResponseEntity.ok(taskService.attachFileToTask(taskId, fileUrl));
    }
}
