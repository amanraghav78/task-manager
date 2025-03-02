package com.example.task_manager.services;

import com.example.task_manager.entities.Task;
import com.example.task_manager.entities.TaskPriority;
import com.example.task_manager.entities.TaskStatus;
import com.example.task_manager.repositories.TaskRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final RedisTemplate<String, Objects> redisTemplate;

    public static final String TASKS_CACHE = "tasks";
    public TaskService(TaskRepository taskRepository, RedisTemplate<String, Objects> redisTemplate){
        this.taskRepository= taskRepository;
        this.redisTemplate = redisTemplate;
    }

    @CachePut(value = TASKS_CACHE, key = "#task.id")
    @CacheEvict(value = TASKS_CACHE, key = "'groupedByStatus'")
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

    public Task attachFileToTask(Long taskId, String fileUrl){
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setFileUrl(fileUrl);
        return taskRepository.save(task);
    }

    public Task createTask(Task task){
        return taskRepository.save(task);
    }

    @Cacheable(value = TASKS_CACHE, key = "'alltasks'")
    public List<Task> getAllTasks(){
        return taskRepository.findAll();
    }

    @Cacheable(value = TASKS_CACHE, key = "#taskId")
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

    @CachePut(value = TASKS_CACHE, key = "#task.id")
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

    @CacheEvict(value = TASKS_CACHE, allEntries = true)
    public void deleteTask(Long id){
        taskRepository.deleteById(id);
    }

    @Cacheable(value = TASKS_CACHE, key = "'groupedByStatus'")
    public Map<TaskStatus, List<Task>> getTasksGroupedByStatus(){
        List<Task> tasks = getAllTasks();
        return tasks.stream().collect(Collectors.groupingBy(Task::getStatus));
    }
}
