package karki.com.karki.services;

import karki.com.karki.entity.Task;
import karki.com.karki.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task updateTask(Long id, Task task) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Zadanie nie zostało znalezione: " + id));
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setTaskType(task.getTaskType());
        existing.setProject(task.getProject());
        existing.setUser(task.getUser());
        return taskRepository.save(existing);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new RuntimeException("Zadanie nie zostało znalezione: " + id);
        }
        taskRepository.deleteById(id);
    }
}
