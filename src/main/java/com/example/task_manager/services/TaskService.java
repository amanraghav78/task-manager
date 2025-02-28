package com.example.task_manager.services;

import com.example.task_manager.entities.Task;
import com.example.task_manager.entities.TaskPriority;
import com.example.task_manager.entities.TaskStatus;
import com.example.task_manager.repositories.TaskRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
//@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository){
        this.taskRepository= taskRepository;
    }

    public Task updateTaskStatus(Long taskId, TaskStatus newStatus) throws Exception {
        Task task = taskRepository.findById(taskId).
                orElseThrow(()-> new Exception());

        validateStatusTransition(task.getStatus(), newStatus);

        task.setStatus(newStatus);
        return taskRepository.save(task);
    }

    private void validateStatusTransition(TaskStatus current, TaskStatus next){
        List<TaskStatus> validTransitions = switch (current){
            case TODO -> List.of(TaskStatus.IN_PROGRESS);
            case IN_PROGRESS -> List.of(TaskStatus.REVIEW, TaskStatus.BLOCKED);
            case REVIEW -> List.of(TaskStatus.COMPLETED, TaskStatus.IN_PROGRESS);
            case BLOCKED -> List.of(TaskStatus.IN_PROGRESS);
            default -> List.of();
        };

        if(!validTransitions.contains(next)){
            throw new IllegalStateException("Invalid transition from " + current + " to " + next);
        }
    }

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id){
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByStatus(TaskStatus status){
        return taskRepository.findByStatus(status);
    }

    public List<Task> getTasksByPriority(TaskPriority priority){
        return taskRepository.findByPriority(priority);
    }

    public List<Task> getTasksByStatusAndPriority(TaskStatus status, TaskPriority priority){
        return taskRepository.findByStatusAndPriority(status, priority);
    }

    public Task updateTask(Long id, Task updatedTask){
        return taskRepository.findById(id).map( task -> {
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            task.setPriority(updatedTask.getPriority());
            return taskRepository.save(task);
                }
        ).orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
    }

    public void deleteTask(Long id){
        taskRepository.deleteById(id);
    }
}
