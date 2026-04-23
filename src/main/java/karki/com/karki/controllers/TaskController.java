package karki.com.karki.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import karki.com.karki.entity.Task;
import karki.com.karki.repository.TaskRepository;
import karki.com.karki.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Zarządzanie zadaniami")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Pobierz wszystkie zadania", description = "Zwraca listę wszystkich zadań")
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @PostMapping
    @Operation(summary = "Utwórz nowe zadanie", description = "Dodaje nowe zadanie do bazy danych")
    public Task createTask(@Parameter(description = "Dane nowego zadania") @RequestBody Task task) {
        return taskService.createTask(task);
    }
}